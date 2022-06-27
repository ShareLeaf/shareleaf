package co.shareleaf.service.content;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.model.*;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.WebsiteProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.parser.ParserService;
import co.shareleaf.service.scraper.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
    private final MetadataRepo metadataRepo;
    private final AWSProps awsProps;
    private final WebsiteProps websiteProps;
    private final ScraperService scraperService;

    @Override
    public Mono<SLContentMetadata> generateContentId(SLContentUrl url) {
        // TODO: delete after testing
//        processUrl(url.getUrl()).map(it -> true);
        // TODO: end delete
        if (isValidUrl(url)) {
            // If a record exist, return its content id. Otherwise,
            // create a new record and kick off the crawling
            // process
            generateHlsManifest();
            return metadataRepo
                    .findByCanonicalUrl(url.getUrl())
                    .map(it -> new SLContentMetadata()
                            .shareableLink(ParserService.getShareableLink(it, websiteProps.getBaseUrl()))
                            .contentId(it.getContentId()))
                    .switchIfEmpty(Mono.defer(() -> processUrl(url.getUrl())));
        }
        return Mono.just(new SLContentMetadata().error(true));
    }

    private void generateHlsManifest() {
        try {
//            ffmpeg -i filename.mp4 -codec: copy -bsf:v h264_mp4toannexb -start_number 0 -hls_time 10 -hls_list_size 0 -f hls filename.m3u8

//            ffmpeg -i audio.aac -c:a copy -hls_segment_type mpegts -map a:0 -hls_time 1 out_aud.m3u8
//            ffmpeg -i video.mp4 -c:v copy -hls_segment_type mpegts -map v:0 -hls_time 1 out_vid.m3u8


//            ffmpeg-3.1.1 -i dual_short.mp4 -i audio_left_short.mp3 -i audio_right_short.mp3 \
//            -threads 0 -muxdelay 0 -y \
//            -map 0:v -map 1 -map 2 -pix_fmt yuv420p -movflags +faststart -r 29.97 -g 60 -refs 1 \
//            -vcodec libx264 -acodec aac -profile:v baseline -level 30 -ar 44100 -ab 64k -f mpegts out.ts

            log.info("DEBUG 1 -- Merging audio and video files...");
            String vid = "/Users/biz/ShareLeaf/shareleaf/backend/shareleaf-api/9lgqYoBv.mpg";
            String audio = "/Users/biz/ShareLeaf/shareleaf/backend/shareleaf-api/9lgqYoBa.mp3";
            String merged =  "/Users/biz/ShareLeaf/shareleaf/backend/shareleaf-api/9lgqYoBa_merged.mp4";
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(String.format("ffmpeg -i %s -i %s -acodec copy -vcodec copy %s", vid, audio, merged));
            log.info("DEBUG 2 -- Merged audio and video files...: {}", process.info().toString());

            log.info("DEBUG 3 Generating HLS files...");
            String hlsOutput = "/Users/biz/ShareLeaf/shareleaf/backend/shareleaf-api/9lgqYoBv.m3u8";
//            ffmpeg -i in.nut -hls_segment_filename 'file%03d.ts' out.m3u8
            process = runtime.exec(String.format("ffmpeg -i %s -codec: copy -start_number 0 -hls_time 5 -hls_list_size 0 -f hls %s", merged, hlsOutput));
            log.info("DEBUG 4 -- Generated HLS files...: {}", process.info().toString());



//            FFmpeg ffmpeg = new FFmpeg();
//            FFprobe ffprobe = new FFprobe();
//            FFmpegBuilder builder = new FFmpegBuilder()
//                    .setInput()     // Filename, or a FFmpegProbeResult
//                    .overrideOutputFiles(true) // Override the output if it exists
//
//                    .addOutput("output.mp4")   // Filename for the destination
//                    .setFormat("mp4")        // Format is inferred from filename, or can be set
//                    .setTargetSize(250_000)  // Aim for a 250KB file
//
//                    .disableSubtitle()       // No subtiles
//
//                    .setAudioChannels(1)         // Mono audio
//                    .setAudioCodec("aac")        // using the aac codec
//                    .setAudioSampleRate(48_000)  // at 48KHz
//                    .setAudioBitRate(32768)      // at 32 kbit/s
//
//                    .setVideoCodec("copy")     // Video using x264
////                    .setVideoFrameRate(24, 1)     // at 24 frames per second
////                    .setVideoResolution(640, 480) // at 640x480 resolution
//
//                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
//                    .done();
//
//            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
//            executor.createJob(builder).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Mono<SLContentMetadata> processUrl(String url) {
        log.info("Processing new url: {}", url);
        MetadataEntity record = new MetadataEntity();
        String contentId = generateUid();
        record.setContentId(contentId);
        record.setCanonicalUrl(url);
        Mono.fromCallable(() -> scraperService.getContent(contentId, url))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(it -> log.error(it.getLocalizedMessage()))
                .subscribe();
        return metadataRepo
                .save(record)
                .doOnError(it -> log.error(it.getLocalizedMessage()))
                .map(it -> new SLContentMetadata()
                        .shareableLink(ParserService.getShareableLink(it, websiteProps.getBaseUrl()))
                        .contentId(contentId));
    }

    @Override
    public Mono<SLContentMetadata> getMetadata(String uid) {
        if (!ObjectUtils.isEmpty(uid)) {
            // Update view count when fetching the metadata for this uid in parallel
            Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(uid);
            metadataEntityMono
                    .flatMap(it -> {
                        it.setViewCount(it.getViewCount()+1);
                        return metadataRepo.save(it);
                    }).subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
            return metadataEntityMono
                    .map(it ->
                            new SLContentMetadata()
                                    .contentId(it.getContentId())
                                    .videoUrl(ParserService.getContentUrl(it, ParserService.VIDEO, awsProps.getCdn()))
                                    .audioUrl(ParserService.getContentUrl(it, ParserService.AUDIO, awsProps.getCdn()))
                                    .imageUrl(ParserService.getContentUrl(it, ParserService.IMAGE, awsProps.getCdn()))
                                    .shareableLink(ParserService.getShareableLink(it, websiteProps.getBaseUrl()))
                                    .mediaType(it.getMediaType() != null ? SLMediaType.fromValue(it.getMediaType()) : null)
                                    .invalidUrl(it.getInvalidUrl())
                                    .processed(it.getProcessed())
                                    .encoding(it.getEncoding())
                                    .title(it.getTitle())
                                    .description(it.getDescription())
                                    .category(it.getCategory())
                                    .shareCount(it.getShareCount())
                                    .viewCount(it.getViewCount())
                                    .likeCount(it.getLikeCount())
                                    .dislikeCount(it.getDislikeCount())
                                    .error(false)
                                    .createdDt(it.getCreatedDt().toEpochSecond(ZoneOffset.UTC))
                    ).switchIfEmpty(Mono.defer(() -> Mono.just(new SLContentMetadata().error(true))));
        }
        return Mono.just(new SLContentMetadata().error(true));
    }

    @Override
    public Mono<SLGenericResponse> incrementShareCount(SLContentId contentId) {
        if (!ObjectUtils.isEmpty(contentId) && !ObjectUtils.isEmpty(contentId.getUid())) {
            return metadataRepo
                    .findByContentId(contentId.getUid())
                    .flatMap(it -> {
                        it.setShareCount(it.getShareCount()+1);
                        return metadataRepo.save(it)
                                .map(res -> new SLGenericResponse().status("OK"));
                    })
                    .switchIfEmpty(Mono.defer(() ->
                            Mono.just(new SLGenericResponse()
                                    .status("Failed to update share count"))));
        }
        return Mono.just(new SLGenericResponse()
                .error("Content ID not provided"));
    }

    public String generateUid() {
        StringBuilder builder = new StringBuilder();
        // An ID length of N gives 62^N unique IDs
        int contentIdLength = 7;
        for (int i = 0; i < contentIdLength; i++) {
            builder.append(getRandomCharacter());
        }
       return builder.toString();
    }

    public Character getRandomCharacter() {
        Random random = new Random();
        String uidAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqprstuvwxyz0123456789";
        int index = random.nextInt(uidAlphabet.length());
        return uidAlphabet.charAt(index);
    }

    private boolean isValidUrl(SLContentUrl url) {
        if (!ObjectUtils.isEmpty(url.getUrl())) {
            try {
                URI uri = new URI(url.getUrl());
                if (!ObjectUtils.isEmpty(uri.getHost())) {
                    return true;
                }
            } catch (URISyntaxException e) {
                return false;
            }
        }
        return false;
    }
}
