package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
public interface ParserService {
   String VIDEO = "video";
   String IMAGE = "image";
   String AUDIO = "audio";

    /**
     * Parse HTML soup and generate structured data for
     * downstream consumption
     * @param soup html soup
     * @param url
     * @param contentId
     * @param client
     */
    void processSoup(String soup, String url, String contentId, WebClient client);

    /**
     * Generate the CDN content url. Every content needs to have an associated image with it,
     * whether that is the content's thumbnail or the content itself is an image. If the
     * content is a video, an audio URL should be generated only if the content has
     * an audio track.
     * @param entity
     * @param mediaType
     * @param cdnBaseUrl
     * @return
     */
    static String getContentUrl(MetadataEntity entity, String mediaType, String cdnBaseUrl) {
        if (mediaType != null && mediaType.equals(IMAGE)) {
            return String.format("%s/%s", cdnBaseUrl, getS3FileName(IMAGE, entity.getContentId()));
        } else if (mediaType != null && mediaType.equals(AUDIO) && entity.getHasAudio()) {
            return String.format("%s/%s", cdnBaseUrl, getS3FileName(AUDIO, entity.getContentId()));
        } else if (mediaType != null && mediaType.equals(ParserService.VIDEO)
                && entity.getMediaType().equals(ParserService.VIDEO)) {
            return String.format("%s/%s", cdnBaseUrl, getS3FileName(VIDEO, entity.getContentId()));
        }
        return "";
    }

    static String getS3FileName(String mediaType, String contentId) {
        if (mediaType.equals(IMAGE)) {
            return contentId + "i.jpg";
        } else if (mediaType.equals(AUDIO)) {
            return contentId + "a.mp4";
        } else if (mediaType.equals(VIDEO)) {
            return contentId + "v.mp4";
        }
        return "";
    }

    /**
     * Generate a shareable link for a given content
     * @param entity
     * @param baseUrl
     * @return
     */
    static String getShareableLink(MetadataEntity entity, String baseUrl) {
        return String.format("%s/%s", baseUrl, entity.getContentId());
    }
}
