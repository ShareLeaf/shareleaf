package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.dto.VideoInfoDto;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
@Slf4j
@Service
public class RedditParser extends BaseParserService implements ParserService {

    public RedditParser(ObjectMapper objectMapper,
                        MetadataRepo metadataRepo,
                        S3Service s3Service,
                        AWSProps awsProps,
                        ScraperUtils scraperUtils) {
        super(objectMapper, metadataRepo, s3Service, awsProps, scraperUtils);
    }

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
                MediaMetadata mediaMetadata = parseUrls(postNode);
                String title = postNode.get("title").asText();
                String permalink = postNode.get("permalink").asText();
                if (mediaMetadata == null) {
                    updateInvalidUrl(contentId, permalink);
                } else {
                    uniquePermalinks.put(permalink, true);
                    updateMetadata(contentId, title, permalink, mediaMetadata);
                    if (!ObjectUtils.isEmpty(mediaMetadata.getAudioUrl())) {
                        downloadContent(contentId, permalink, mediaMetadata.getAudioUrl(), AUDIO);
                    }
                    if (!ObjectUtils.isEmpty(mediaMetadata.getImageUrl())) {
                        downloadContent(contentId, permalink, mediaMetadata.getImageUrl(), IMAGE);
                    }
                    if (!ObjectUtils.isEmpty(mediaMetadata.getVideoUrl())) {
                        downloadContent(contentId, permalink, mediaMetadata.getVideoUrl(), VIDEO);
                    }
                    if (!ObjectUtils.isEmpty(mediaMetadata.getGifUrl())) {
                        downloadContent(contentId, permalink, mediaMetadata.getGifUrl(), VIDEO);
                    }
                }
            }
        } catch (Exception e) {
            updateInvalidUrl(contentId, url);
            log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void processUrlV2(String igUrl, String contentId) {
        log.info("NOT IMPLEMENTED");
    }


    private MediaMetadata parseUrls(JsonNode postNode) {
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
        return new MediaMetadata(imageUrl, gifUrl, videoUrl, audioUrl, dbMediaType, encoding, null);
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
