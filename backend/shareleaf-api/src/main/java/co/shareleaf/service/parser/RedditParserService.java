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
public class RedditParserService implements ParserService{
    private final ObjectMapper objectMapper;
    private final MetadataRepo metadataRepo;
    private final S3Service s3Service;
    private final AWSProps awsProps;
    private final ScraperUtils scraperUtils;
    private final String GIF = "gif";
    private final String VIDEO = "video";
    private final String IMAGE = "image";
    private final String AUDIO = "audio";

    @Override
    public void processSoup(String soup, String url, String contentId, WebClient client) {
        String postId = url.substring(url.indexOf("comments/") + 9);
        postId = "t3_" + postId.substring(0, postId.indexOf("/"));
        Document doc = Jsoup.parse(soup);
        try {
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
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getGifUrl(), GIF));
                    }
                    if (!tasks.isEmpty()) {
                        // Run all tasks asynchronously
                        Flux.merge(tasks).subscribe();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Mono<Boolean> downloadContent(String contentId, String permalink, String mediaUrl, String mediaType) {
        try {
            UnexpectedPage page = scraperUtils.getWebClient(Platform.REDDIT).getPage(mediaUrl);
            String file = contentId + "_i";
            String contentType = S3Service.IMAGE_TYPE;
            if (mediaType.equals(VIDEO)) {
                file = contentId + "_v";
                contentType = S3Service.VIDEO_TYPE;
            } else if (mediaType.equals(AUDIO)) {
                file = contentId + "_a";
                contentType = S3Service.AUDIO_TYPE;
            } else if (mediaType.equals(GIF)) {
                file = contentId + "_g";
                contentType = S3Service.GIF_TYPE;
            }
            try (InputStream in = page.getInputStream()) {
                s3Service.uploadImage(awsProps.getBucket(), file, in, contentType);
                updateProgress(contentId, true);
            }
            return Mono.just(true);
        } catch (Exception e) {
            e.printStackTrace();
            // If not an exception is raised on a video download attempt, then
            // assume it's an invalid URL. It's okay if an audio download fails
            // because not all videos (and certainly not GIFs) would have videos
            if (mediaUrl.contains("_v.mp4") || mediaUrl.contains("_i.jpg")) {
                updateInvalidUrl(contentId, permalink);
            }
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
        String encoding, dbMediaType, imageUrl = "", audioUrl = "", videoUrl = "", gifUrl = "";
        if (parsedMediaType == null || parsedMediaType.equals("embed")) {
            return null;
        }
        if (parsedMediaType.equals("image")) {
            dbMediaType = IMAGE;
            encoding = "jpg";
            imageUrl = mediaNode.get("content").asText();
        } else if (parsedMediaType.equals("gifvideo")) {
            dbMediaType = GIF;
            encoding = "gif";
            gifUrl = mediaNode.get("content").asText();
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
                dbMediaType = GIF;
                encoding = "gif";
                dashUrl = mediaNode.get("dashUrl").asText();
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
            if (dbMediaType.equals(GIF)) {
                gifUrl = videoUrl;
                videoUrl = "";
                audioUrl = "";
            }
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
        log.info("Invalid URL encountered for contentId {}: {}", contentId, permalink);
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
}
