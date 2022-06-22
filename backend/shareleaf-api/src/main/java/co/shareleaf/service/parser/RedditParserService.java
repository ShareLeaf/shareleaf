package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedditParserService implements ParserService{
    private final ObjectMapper objectMapper;
    private final MetadataRepo metadataRepo;
    private final String GIF = "gif";
    private final String VIDEO = "video";
    private final String IMAGE = "image";
    private final String AUDIO = "audio";

    @Override
    public void processSoup(String soup, String url, String contentId) {
        String postId = url.substring(url.indexOf("comments/") + 9);
        postId = "t3_" + postId.substring(0, postId.indexOf("/"));
        Document doc = Jsoup.parse(soup);
        try {
            String json = doc.select("script").dataNodes().get(6).getWholeData().replace("window.___r = ", "");
            JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
            JsonNode postNode = jsonNode.get("posts").get("models").get(postId);
            MediaUrl mediaUrl = parseUrls(postNode);
            String title = postNode.get("title").asText();
            String permalink = postNode.get("permalink").asText();
            if (mediaUrl == null) {
                updateInvalidUrl(contentId, permalink);
            } else {
                updateMetadata(contentId, title, permalink, mediaUrl);
                if (mediaUrl.getAudioUrl() != null) {
                    downloadContent(contentId, mediaUrl.getAudioUrl(), AUDIO);
                }
                if (mediaUrl.getImageUrl() != null) {
                    downloadContent(contentId, mediaUrl.getImageUrl(), IMAGE);
                }
                if (mediaUrl.getVideoUrl() != null) {
                    downloadContent(contentId, mediaUrl.getVideoUrl(), VIDEO);
                }
                if (mediaUrl.getGifUrl() != null) {
                    downloadContent(contentId, mediaUrl.getGifUrl(), GIF);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MediaUrl parseUrls(JsonNode postNode) {
        String parsedMediaType = getMediaType(postNode);
        String encoding, dbMediaType, imageUrl = "", audioUrl = "", videoUrl = "", gifUrl = "";
        if (parsedMediaType == null) {
            return null;
        }
        if (parsedMediaType.equals("image")) {
            dbMediaType = IMAGE;
            encoding = "jpg";
            imageUrl = postNode.get("content").asText();
        } else if (parsedMediaType.equals("gifvideo")) {
            dbMediaType = GIF;
            encoding = "gif";
            gifUrl = postNode.get("content").asText();
        } else {
            JsonNode mediaNode = postNode.get("media");
            String dashUrl = mediaNode.get("dashUrl").asText();
            dashUrl = dashUrl.substring(0, dashUrl.indexOf("DASH") + 4);
            dbMediaType = VIDEO;
            encoding = "video/mp4";
            int height = 0;
            JsonNode heightNode = mediaNode.get("height");
            if (heightNode != null) {
                height = heightNode.asInt();
            }
            if (parsedMediaType.equals("gif")) {
                dbMediaType = GIF;
                encoding = "gif";
            } else if (parsedMediaType.equals("videoPreview")) {
                dashUrl = mediaNode.get("videoPreview").get("dashUrl").asText();
                height =  mediaNode.get("videoPreview").get("height").asInt();
            } else if (parsedMediaType.equals("video")) {
                audioUrl = dashUrl + "_audio.mp4";
            }
            videoUrl = dashUrl + "_" + height + ".mp4";
        }
        return new MediaUrl(imageUrl, gifUrl, videoUrl, audioUrl, dbMediaType, encoding);
    }

    private void updateMetadata(String contentId, String title, String permalink, MediaUrl mediaUrl) {
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
                    it.setEncoding(mediaUrl.getEncoding());
                    it.setMediaType(mediaUrl.getMediaType());
                    it.setTitle(title);
                    it.setCanonicalUrl(permalink);
                    it.setUpdatedDt(LocalDateTime.now());
                    return metadataRepo.save(it);
                }).subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void updateInvalidUrl(String contentId, String permalink) {
        Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(contentId);
        metadataEntityMono
                .flatMap(it -> {
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

    @Override
    public void uploadContent() {

    }
}
