package co.shareleaf.service.parser;


import static co.shareleaf.service.parser.BaseParserService.VIDEO;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.dto.VideoInfoDto;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.actions.media.MediaAction;
import co.shareleaf.instagram4j.models.media.timeline.TimelineVideoMedia;
import co.shareleaf.instagram4j.responses.media.MediaInfoResponse;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.ZenRowsProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.Platform;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author Bizuwork Melesse
 * created on 7/9/22
 */
@Slf4j
@Service
public class InstagramParser extends BaseParserService implements ParserService {
    private final ZenRowsProps zenRowsProps;

    public InstagramParser(ObjectMapper objectMapper,
        MetadataRepo metadataRepo,
        S3Service s3Service,
        AWSProps awsProps,
        ScraperUtils scraperUtils,
        ZenRowsProps zenRowsProps) {
        super(objectMapper, metadataRepo, s3Service, awsProps, scraperUtils);
        this.zenRowsProps = zenRowsProps;

    }


    @Override
    public void processSoup(String soup, String url, String contentId, WebClient client) {
        soup = soup.replace("\\", "");
        if (isValidSoup(soup)) {
            Document doc = Jsoup.parse(soup);
            JsonNode media = getMediaNode(doc, soup, contentId, url);
            MediaMetadata mediaMetadata;
            if (media != null) {
                mediaMetadata = parseFromMediaNode(media);
            } else {
                mediaMetadata = parseFromMediaResponse(soup);
            }
            VideoInfoDto dto = getDtoFromMediaMetadata(mediaMetadata, url, contentId);
            if (dto != null) {
                downloadContent(contentId, url, dto.getUrl(), VIDEO);
            }
        }
    }

    private VideoInfoDto getDtoFromMediaMetadata(MediaMetadata mediaMetadata, String igUrl, String contentId) {
        if (mediaMetadata != null) {
            if (!ObjectUtils.isEmpty(mediaMetadata.getVideoUrl())) {
                VideoInfoDto dto = new VideoInfoDto();
                dto.setUrl(mediaMetadata.getVideoUrl());
                dto.setCaption(mediaMetadata.getDescription());
                dto.setImageUrl(mediaMetadata.getImageUrl());
                dto.setPermalink(igUrl);
                dto.setVideoIdOverride(contentId);
                return dto;
            }
        } else {
            log.warn("Warning in InstagramParser.processSoup: Failed to process contentId={} url={} " +
                    "because media metadata is not found",
                contentId,
                igUrl);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void processUrlV2(String igUrl, String contentId) {
        log.info("About to process URL for Instagram with content ID {}: {}", contentId, igUrl);
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = new URIBuilder()
            .setScheme("https").setHost(zenRowsProps.getHost()).setPath(zenRowsProps.getPath())
            .setParameter("apikey", zenRowsProps.getApikey())
            .setParameter("url", igUrl)
            .setParameter("js_render", "true")
            .setParameter("premium_proxy", "true")
            .setParameter("autoparse", "true")
            .build();
        HttpGet httpGet = new HttpGet(uri);
        HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
        JsonNode media = objectMapper.readValue(httpEntity.getContent(), JsonNode.class);
        if (media != null ) {
            MediaMetadata mediaMetadata = parseFromMediaNode(media.get("shortcode_media"));
            VideoInfoDto dto = getDtoFromMediaMetadata(mediaMetadata, igUrl, contentId);
            if (dto != null && mediaMetadata != null) {
                downloadContent(contentId, igUrl, dto.getUrl(), VIDEO);
                updateMetadata(contentId, dto.getCaption(), igUrl, mediaMetadata);
                generateHlsManifest(contentId);
                s3Service.uploadHlsData(awsProps.getBucket(), contentId, igUrl);
            }
        }
    }

    private MediaMetadata parseFromMediaResponse(String soup) {
        int indexOf = soup.indexOf("media_id");
        int length = 100;
        if (indexOf >= 0) {
            // media_id doesn't seem to follow a fixed pattern so extract it as a substring
            String substring = soup.substring(indexOf, indexOf + length);
            String pattern = "[0-9]+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(substring);
            JsonNode node = null;
            String mediaId = null;
            try {
                if (m.find()) {
                    node = objectMapper.readValue(m.group(0), JsonNode.class);
                }
                if (node != null) {
                    mediaId = node.asText();
                    MediaAction action = new MediaAction(IGClient.builder().build(), mediaId);
                    MediaInfoResponse mediaInfoResponse = action.info().get();
                    String videoUrl = "", imageUrl = "", description = "";
                    if (!ObjectUtils.isEmpty(mediaInfoResponse.getItems())) {
                        TimelineVideoMedia tvm = (TimelineVideoMedia) mediaInfoResponse.getItems().get(0);
                        imageUrl = tvm.getImage_versions2().getCandidates().get(0).getUrl();
                        videoUrl = tvm.getVideo_versions().get(0).getUrl();
                        description = tvm.getCaption().getText();
                    }
                    return new MediaMetadata(imageUrl, "", videoUrl, "", VIDEO, "video/mp4", description);
                }
            } catch (Exception e) {
                // TODO: post to slack
                log.error("Error in InstagramParser.parseFromMediaResponse: {}", e.getLocalizedMessage());
            }
        }

        return null;
    }

    private MediaMetadata parseFromMediaNode(@NonNull JsonNode media) {
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
            log.error("Error in InstagramParser.parseFromMediaNode: {}", e.getLocalizedMessage());
        }
        if (videoUrlNode != null) {
            String videoUrl = videoUrlNode.asText();
            String imageUrl = "";
            if (imageUrlNode != null) {
                imageUrl = imageUrlNode.asText();
            }
            return new MediaMetadata(imageUrl, "", videoUrl, null, VIDEO, "video/mp4", description);
        }
        return null;
    }

    private JsonNode getMediaNode(Document doc, String soup, String contentId, String url) {
        if (soup.contains("not-logged-in")) {
            // TODO: post to slack
            log.error("Error in InstagramParser.getMediaNode Login required");
        } else {
            try {
                String json = null;
                List<DataNode> dataNodes = doc.select("script").dataNodes();
                for (DataNode dataNode : dataNodes) {
                    String wholeData = dataNode.getWholeData();
                    if (wholeData.startsWith("window._sharedData")) {
                        json = wholeData.replace("window._sharedData =", "").replace("\n", "").strip();
                        break;
                    }
                }
                if (json != null) {
                    JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                    return jsonNode
                        .get("entry_data")
                        .get("PostPage")
                        .get(0)
                        .get("graphql")
                        .get("shortcode_media");
                }

            } catch (JsonProcessingException | NullPointerException e) {
                // TODO: post to slack
                log.error("Error in InstagramParser.getMediaNode Failed to media node for contentId={} and url={}",
                    contentId,
                    url);
            }
        }
        return null;
    }

    private boolean isValidSoup(String soup) {
        if (soup.contains("not-logged-in")) {
            // TODO: post to slack
            log.error("Error in InstagramParser.getMediaNode: Login required");
            return false;
        }
        return true;
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
