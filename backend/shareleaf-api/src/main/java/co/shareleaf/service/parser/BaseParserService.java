package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Bizuwork Melesse
 * created on 6/26/22
 */
@Slf4j
public abstract class BaseParserService {
    public static final String VIDEO = "video";
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";

    /**
     * Generate HTTP Live Streaming (HLS) files and upload them to S3.
     * HTML5 video player uses the HLS index generated in this process
     * to stream the media content to the client.
     *
     * @param contentId
     */
    @SneakyThrows
    public void generateHlsManifest(String contentId) {
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
}
