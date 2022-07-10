package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.Platform;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedditIBaseParserService extends BaseParserService implements ParserService {
    private final ObjectMapper objectMapper;
    private final MetadataRepo metadataRepo;
    private final S3Service s3Service;
    private final AWSProps awsProps;
    private final ScraperUtils scraperUtils;

    @Override
    public void processSoup(String soup, String url, String contentId, WebClient client) {
        log.info("Processing soup for Reddit with content ID {}: {}", contentId, url);
        try {
            String postId = url.substring(url.indexOf("comments/") + 9);
            postId = "t3_" + postId.substring(0, postId.indexOf("/"));
            Document doc = Jsoup.parse(soup);
            String json = null;
            List<DataNode> dataNodes = doc.select("script").dataNodes();
            for (DataNode dataNode : dataNodes) {
                String wholeData = dataNode.getWholeData();
                if (wholeData.startsWith("window.___r")) {
                    json = wholeData.replace("window.___r = ", "");
                    break;
                }
            }
            if (json != null) {
                JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                JsonNode postNode = jsonNode.get("posts").get("models").get(postId);
                MediaUrl mediaUrl = parseUrls(postNode);
                String title = postNode.get("title").asText();
                String permalink = postNode.get("permalink").asText();
                if (mediaUrl == null) {
                    updateInvalidUrl(contentId, permalink);
                } else {
                    uniquePermalinks.put(permalink, true);
                    updateMetadata(contentId, title, permalink, mediaUrl);
                    List<Mono<Boolean>> tasks = new ArrayList<>();
                    if (!ObjectUtils.isEmpty(mediaUrl.getAudioUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getAudioUrl(), AUDIO));
                    }
                    if (!ObjectUtils.isEmpty(mediaUrl.getImageUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getImageUrl(), IMAGE));
                    }
                    if (!ObjectUtils.isEmpty(mediaUrl.getVideoUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getVideoUrl(), VIDEO));
                    }
                    if (!ObjectUtils.isEmpty(mediaUrl.getGifUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getGifUrl(), VIDEO));
                    }
                    if (!tasks.isEmpty()) {
                        // Run all tasks asynchronously
                        Flux.merge(tasks).doOnComplete(() -> {
                            log.trace("Determining if should run hls {} {}", mediaUrl.getGifUrl(), mediaUrl.getVideoUrl());
                            if (!ObjectUtils.isEmpty(mediaUrl.getGifUrl()) ||
                                    !ObjectUtils.isEmpty(mediaUrl.getVideoUrl())) {
                                generateHlsManifest(contentId, permalink);
                                s3Service.uploadHlsData(awsProps.getBucket(), contentId, permalink);
                            } else {
                                log.trace("Will not be running HLS on {}", contentId);
                            }
                        }).subscribe();
                    }
                }
            }
        } catch (Exception e) {
            updateInvalidUrl(contentId, url);
           log.error(e.getLocalizedMessage());
        }
    }

    private Mono<Boolean> downloadContent(String contentId, String permalink, String mediaUrl, String mediaType) {
        String contentType = S3Service.IMAGE_TYPE;
        if (uniquePermalinks.containsKey(permalink)) {
            try {
                log.info("Downloading content for content ID {}: permalink={} mediaUrl={} mediaType={}",
                        contentId, permalink, mediaUrl, mediaType);
                UnexpectedPage page = scraperUtils.getWebClient(Platform.REDDIT).getPage(mediaUrl);
                String file = getS3FileName(IMAGE, contentId);
                if (mediaType.equals(VIDEO)) {
                    file = getS3FileName(VIDEO, contentId);
                    contentType = S3Service.VIDEO_TYPE;
                } else if (mediaType.equals(AUDIO)) {
                    file = getS3FileName(AUDIO, contentId);
                    contentType = S3Service.AUDIO_TYPE;
                }
                try (InputStream in = page.getInputStream()) {
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

    private void updateProgress(String contentId, boolean completed) {
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
                    it.setProcessed(completed);
                    it.setUpdatedDt(LocalDateTime.now());
                    return metadataRepo.save(it);
                }).subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private MediaUrl parseUrls(JsonNode postNode) {
        String parsedMediaType = getMediaType(postNode);
        JsonNode mediaNode = postNode.get("media");
        boolean isGif = false;
        String encoding, dbMediaType, imageUrl = "", audioUrl = "", videoUrl = "", gifUrl = "";
        if (parsedMediaType == null || parsedMediaType.equals("embed")) {
            return null;
        }
        if (parsedMediaType.equals("image")) {
            dbMediaType = IMAGE;
            encoding = "jpg";
            imageUrl = mediaNode.get("content").asText();
        } else if (parsedMediaType.equals("gifvideo")) {
            dbMediaType = VIDEO;
            encoding = "video/mp4";
            gifUrl = mediaNode.get("content").asText();
            isGif = true;
        } else {
            String dashUrl = "";
            dbMediaType = VIDEO;
            encoding = "video/mp4";
            int height = 0;
            JsonNode heightNode = mediaNode.get("height");
            if (heightNode != null) {
                height = heightNode.asInt();
            }
            if (parsedMediaType.equals("gif")) {
                dbMediaType = VIDEO;
                encoding = "video/mp4";
                dashUrl = mediaNode.get("dashUrl").asText();
                isGif = true;
            } else if (parsedMediaType.equals("videoPreview")) {
                dashUrl = mediaNode.get("videoPreview").get("dashUrl").asText();
                height =  mediaNode.get("videoPreview").get("height").asInt();
            } else if (parsedMediaType.equals("video")) {
                dashUrl = mediaNode.get("dashUrl").asText();
            }
            // Get thumbnail URL
            JsonNode posterUrlNode = mediaNode.get("posterUrl");
            if (posterUrlNode != null) {
                imageUrl = posterUrlNode.asText();
            }
            dashUrl = dashUrl.substring(0, dashUrl.indexOf("DASH") + 4);
            videoUrl = dashUrl + "_" + height + ".mp4";
            audioUrl = dashUrl + "_audio.mp4";
            if (isGif) {
                gifUrl = videoUrl;
                videoUrl = "";
                audioUrl = "";
            }
        }
        return new MediaUrl(imageUrl, gifUrl, videoUrl, audioUrl, dbMediaType, encoding);
    }

    private void updateMetadata(String contentId, String title, String permalink, MediaUrl mediaUrl) {
        log.info("Updating metadata for content ID {}: title={} mediaType={} encoding={} permalink={}",
                contentId, title, mediaUrl.getMediaType(), mediaUrl.getEncoding(), permalink);
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
                                uniquePermalinks.remove(permalink);
                            } else {
                                newEntity.setEncoding(mediaUrl.getEncoding());
                                newEntity.setMediaType(mediaUrl.getMediaType());
                                newEntity.setHasAudio(!ObjectUtils.isEmpty(mediaUrl.getAudioUrl()));
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
//        metadataEntityMono
//                .map(entity ->
//                    // Check if a duplicate entity exists with the same permalink
//                    metadataRepo.findByCanonicalUrl(permalink)
//                            .switchIfEmpty(Mono.defer(() -> {
//                                entity.setEncoding(mediaUrl.getEncoding());
//                                entity.setMediaType(mediaUrl.getMediaType());
//                                entity.setHasAudio(!ObjectUtils.isEmpty(mediaUrl.getAudioUrl()));
//                                entity.setTitle(title);
//                                entity.setCanonicalUrl(permalink);
//                                entity.setUpdatedDt(LocalDateTime.now());
//                                return metadataRepo.save(entity);
//                            })))
//                .subscribeOn(Schedulers.boundedElastic())
//                .subscribe();
    }

    private void updateInvalidUrl(String contentId, String permalink) {
        log.error("Invalid URL encountered for contentId {}: {}", contentId, permalink);
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
                    it.setProcessed(false);
                    it.setInvalidUrl(true);
                    it.setCanonicalUrl(permalink);
                    it.setUpdatedDt(LocalDateTime.now());
                    return metadataRepo.save(it);
                }).subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private String getMediaType(JsonNode postNode) {
        JsonNode mediaNode = postNode.get("media");
        if (mediaNode.get("isGif") != null && mediaNode.get("isGif").asBoolean()) {
            return "gif";
        }
        JsonNode parsedType = mediaNode.get("type");
        String type = null;
        if (parsedType != null) {
            type = parsedType.asText();
        }
        return type;
    }
}
