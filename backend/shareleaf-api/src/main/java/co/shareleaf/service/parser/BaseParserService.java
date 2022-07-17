package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 6/26/22
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseParserService {
    public static final String VIDEO = "video";
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public final ObjectMapper objectMapper;
    public final MetadataRepo metadataRepo;
    public final S3Service s3Service;
    public final AWSProps awsProps;
    public final ScraperUtils scraperUtils;
    public final IGClient igClient;

    /**
     * Generate HTTP Live Streaming (HLS) files and upload them to S3.
     * HTML5 video player uses the HLS index generated in this process
     * to stream the media content to the client.
     *
     * @param contentId
     * @param permalink
     */
    @SneakyThrows
    public void generateHlsManifest(String contentId, String permalink) {
        if (ParserService.uniquePermalinks.containsKey(permalink)) {
            String videoFile = getS3FileName(VIDEO, contentId);
            String audioFile = getS3FileName(AUDIO, contentId);

            // Merge the audio and video streams, if applicable
            String mergedFile = contentId + "_merged.mp4";
            log.trace("Determining if should generate HLS files for {}", contentId);
            if (Files.exists(Path.of(audioFile)) && Files.exists(Path.of(videoFile))) {
                Runtime runtime = Runtime.getRuntime();
                try {
                    log.info("Merging audio and video streams for content ID {}", contentId);
                    runtime.exec(String.format("ffmpeg -i %s -i %s -acodec copy -vcodec copy %s",
                                    videoFile, audioFile, mergedFile))
                            .waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mergedFile = videoFile;
                log.trace("No audio found so using video as merged file {}", mergedFile);
            }
            // Generate HLS segments
            String hlsOutput = contentId + ".m3u8";
            Runtime runtime = Runtime.getRuntime();
            try {
                log.info("Generating HLS segments for content ID {}", contentId);
                runtime.exec(String.format("ffmpeg -i %s -codec: copy -start_number 0 -hls_time 2 -hls_list_size 0 -f hls %s",
                                mergedFile, hlsOutput))
                        .waitFor();
                log.info("Done with generating HLS segments for content ID {}", contentId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate the CDN content url. Every content needs to have an associated image with it,
     * whether that is the content's thumbnail or the content itself is an image. If the
     * content is a video, an audio URL should be generated only if the content has
     * an audio track.
     * @param contentId
     * @param mediaType
     * @param cdnBaseUrl
     * @return
     */
    public String getContentUrl(String contentId, String mediaType, String cdnBaseUrl) {
        if (mediaType != null && mediaType.equals(IMAGE)) {
            return String.format("%s/%s", cdnBaseUrl, getS3FileName(IMAGE, contentId));
        } else if (mediaType != null && mediaType.equals(VIDEO)) {
            return String.format("%s/%s/%s.m3u8", cdnBaseUrl, contentId, contentId);
        }
        return "";
    }

    public String getS3FileName(String mediaType, String contentId) {
        if (mediaType.equals(IMAGE)) {
            return contentId + "i";
        } else if (mediaType.equals(AUDIO)) {
            return contentId + "a";
        } else if (mediaType.equals(VIDEO)) {
            return contentId + "v";
        }
        return "";
    }

    /**
     * Generate a shareable link for a given content
     * @param entity
     * @param baseUrl
     * @return
     */
    public String getShareableLink(MetadataEntity entity, String baseUrl) {
        return String.format("%s/%s", baseUrl, entity.getContentId());
    }

    public Mono<Boolean> downloadContent(String contentId, String permalink, String mediaUrl, String mediaType) {
        String contentType = S3Service.IMAGE_TYPE;
        if (ParserService.uniquePermalinks.containsKey(permalink)) {
            try {
                log.info("Downloading content for content ID {}: permalink={} mediaUrl={} mediaType={}",
                        contentId, permalink, mediaUrl, mediaType);
                Response response = igClient.getHttpClient().newCall(
                        new Request
                                .Builder()
                                .url(mediaUrl)
                                .build()).execute();
                String file = getS3FileName(IMAGE, contentId);
                if (mediaType.equals(VIDEO)) {
                    file = getS3FileName(VIDEO, contentId);
                    contentType = S3Service.VIDEO_TYPE;
                } else if (mediaType.equals(AUDIO)) {
                    file = getS3FileName(AUDIO, contentId);
                    contentType = S3Service.AUDIO_TYPE;
                }
                try (InputStream in = response.body().byteStream()) {
                    // If the content is an image, upload it to S3 immediately
                    log.trace("About to upload or process {} {}", file, contentType);
                    if (contentType.equals(S3Service.IMAGE_TYPE)) {
                        s3Service.uploadImage(awsProps.getBucket(), file, in, contentType);
                    } else {
                        // Save the file to disk for processing
                        File output = new File(file);
                        Files.copy(in, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    updateProgress(contentId, true);
                }
                return Mono.just(true);
            } catch (Exception e) {
                e.printStackTrace();
                // An audio download may fail because it may not be available. But a video or
                // image download should never fail
                if (!contentType.equals(S3Service.AUDIO_TYPE)) {
                    updateInvalidUrl(contentId, permalink);
                }
                updateProgress(contentId, false);
            }
        } else {
            log.warn("Cancelling content download for {}", permalink);
        }
        return Mono.just(false);
    }

    public void updateProgress(String contentId, boolean completed) {
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
                    it.setProcessed(completed);
                    it.setUpdatedDt(LocalDateTime.now());
                    return metadataRepo.save(it);
                }).subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public void updateMetadata(String contentId, String title, String permalink, MediaMetadata mediaMetadata) {
        log.info("Updating metadata for content ID {}: title={} mediaType={} encoding={} permalink={}",
                contentId, title, mediaMetadata.getMediaType(), mediaMetadata.getEncoding(), permalink);
        Mono<MetadataEntity> newEntityMono = metadataRepo.findByContentId(contentId)
                .switchIfEmpty(Mono.defer(() -> Mono.just(new MetadataEntity())));
        Mono<MetadataEntity> duplicateEntityMono = metadataRepo.findByCanonicalUrl(permalink)
                .switchIfEmpty(Mono.defer(() -> Mono.just(new MetadataEntity())));;
        Mono.zip(newEntityMono, duplicateEntityMono)
                .flatMap(data -> {
                    // Update the entry only if there is no duplicate entity
                    MetadataEntity newEntity = data.getT1();
                    MetadataEntity duplicateEntity = data.getT2();
                    if (!ObjectUtils.isEmpty(duplicateEntity.getContentId()) &&
                            !duplicateEntity.getContentId().equals(newEntity.getContentId())) {
                        // This permalink has already been processed so we must
                        // remove it. The content ID should be added as an alias
                        // to the existing metadata
                        log.warn("Content already exists for {}: ", permalink);
                        // TODO: add a table to map aliases
                        ParserService.uniquePermalinks.remove(permalink);
                    } else {
                        newEntity.setEncoding(mediaMetadata.getEncoding());
                        newEntity.setMediaType(mediaMetadata.getMediaType());
                        newEntity.setHasAudio(!ObjectUtils.isEmpty(mediaMetadata.getAudioUrl()));
                        newEntity.setTitle(title);
                        newEntity.setCanonicalUrl(permalink);
                        newEntity.setUpdatedDt(LocalDateTime.now());
                        log.info("Updating new metadata entity for content ID {}", contentId);
                        return metadataRepo.save(newEntity);
                    }
                    return Mono.just(true);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }

    public void updateInvalidUrl(String contentId, String permalink) {
        log.error("Invalid URL encountered for contentId {}: {}", contentId, permalink);
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
                    it.setProcessed(false);
                    it.setInvalidUrl(true);
                    it.setCanonicalUrl(permalink);
                    it.setUpdatedDt(LocalDateTime.now());
                    return metadataRepo.save(it);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public void submitTasks(List<Mono<Boolean>> tasks, MediaMetadata mediaMetadata, String contentId, String permalink) {
        if (!tasks.isEmpty()) {
            // Run all tasks asynchronously
            Flux.merge(tasks).doOnComplete(() -> {
                log.trace("Determining if should run hls {} {}", mediaMetadata.getGifUrl(), mediaMetadata.getVideoUrl());
                if (!ObjectUtils.isEmpty(mediaMetadata.getGifUrl()) ||
                        !ObjectUtils.isEmpty(mediaMetadata.getVideoUrl())) {
                    generateHlsManifest(contentId, permalink);
                    s3Service.uploadHlsData(awsProps.getBucket(), contentId, permalink);
                } else {
                    log.trace("Will not be running HLS on {}", contentId);
                }
            }).subscribe();
        }
    }
}
