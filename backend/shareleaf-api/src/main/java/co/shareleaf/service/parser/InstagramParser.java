package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bizuwork Melesse
 * created on 7/9/22
 */
@Slf4j
@Service
public class InstagramParser extends BaseParserService implements ParserService {

    public InstagramParser(ObjectMapper objectMapper,
                           MetadataRepo metadataRepo,
                           S3Service s3Service, AWSProps awsProps,
                           ScraperUtils scraperUtils,
                           IGClient igClient) {
        super(objectMapper, metadataRepo, s3Service, awsProps, scraperUtils, igClient);
    }

    @Override
    public void processSoup(String soup, String url, String contentId, WebClient client) {
        downloadMedia(url, contentId);
    }

    private void downloadMedia(String url, String contentId) {
        try {
            var response = igClient.getHttpClient().newCall(
                    new Request.Builder()
                            .url(url).
                            build())
                    .execute();
            Document doc = Jsoup.parse(response.body().string());
            Map<String, String> metadata = parseMetadata(doc);
            String json = null;
            List<DataNode> dataNodes = doc.select("script").dataNodes();
            for (DataNode dataNode : dataNodes) {
                String wholeData = dataNode.getWholeData();
                if (wholeData.startsWith("window._sharedData")) {
                    json = wholeData.replace("window._sharedData =", "").replace("\n", "").strip();
                    break;
                }
            }
            JsonNode jsonNode = null;
            if (json != null) {
                jsonNode = objectMapper.readValue(json, JsonNode.class);
                JsonNode media = jsonNode
                        .get("entry_data")
                        .get("PostPage")
                        .get(0)
                        .get("graphql")
                        .get("shortcode_media");
                JsonNode imageUrlNode = media
                        .get("display_url");
                JsonNode videoUrlNode = media
                        .get("video_url");
                String description = "";
                try {
                    JsonNode captionNode = media
                            .get("edge_media_to_caption")
                            .get("edges");
                    if (captionNode.size() > 0) {
                        description = captionNode.get(0)
                                .get("node")
                                .get("text").asText();
                    }
                } catch (Exception e) {
                    log.error("InstagramParser.downloadMedia: {}", e.getLocalizedMessage());
                }

                if (videoUrlNode != null) {
                    String videoUrl = videoUrlNode.asText();
                    String imageUrl = "";
                    if (imageUrlNode != null) {
                        imageUrl = imageUrlNode.asText();
                    }
                    String permalink = metadata.getOrDefault("permalink", url);
                    uniquePermalinks.put(permalink, true);
                    MediaUrl mediaUrl = new MediaUrl(imageUrl, "", videoUrl, null, VIDEO, "video/mp4");
                    updateMetadata(contentId, metadata.getOrDefault("title", description),
                            permalink, mediaUrl);
                    List<Mono<Boolean>> tasks = new ArrayList<>();
                    if (!ObjectUtils.isEmpty(mediaUrl.getImageUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getImageUrl(), IMAGE));
                    }
                    if (!ObjectUtils.isEmpty(mediaUrl.getVideoUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getVideoUrl(), VIDEO));
                    }
                    submitTasks(tasks, mediaUrl, contentId, permalink);
                }
            }
        } catch (Exception e) {
            updateInvalidUrl(contentId, url);
            log.error("InstagramParser.downloadMedia: {}", e.getLocalizedMessage());
        }
    }

    private Map<String, String> parseMetadata(Document doc) {
        Map<String, String> parsed = new HashMap<>();
        if (!ObjectUtils.isEmpty(doc.children())) {
            for (Element child : doc.head().children()) {
                if (child.tag().toString().equals("link") &&
                        !ObjectUtils.isEmpty(child.attributes().get("rel")) &&
                        child.attributes().get("rel").equals("canonical")) {
                    parsed.put("permalink", child.attributes().get("href"));
                } else if (child.tag().toString().equals("title")) {
                    if (child.childNodes().size() > 0) {
                        parsed.put("title", child.childNodes().get(0).toString().strip());
                    }
                }
            }
        }
        return parsed;
    }
}
