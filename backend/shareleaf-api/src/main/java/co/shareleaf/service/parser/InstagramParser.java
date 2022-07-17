package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        soup = soup.replace("\\", "");
        if (isValidSoup(soup)) {
            Document doc = Jsoup.parse(soup);
            Map<String, String> pageMetadata = parseMetadata(doc);
            JsonNode media = getMediaNode(doc, soup, contentId, url);
            MediaMetadata mediaMetadata;
            if (media != null) {
                mediaMetadata = parseFromMediaNode(media);
            } else {
                mediaMetadata = parseFromMediaResponse(soup, client, contentId, url);
            }

            if (mediaMetadata != null) {
                if (!ObjectUtils.isEmpty(mediaMetadata.getVideoUrl())) {
                    String permalink = pageMetadata.getOrDefault("permalink", url);
                    uniquePermalinks.put(permalink, true);
                    updateMetadata(contentId, pageMetadata.getOrDefault("title", mediaMetadata.getDescription()),
                            permalink, mediaMetadata);
                    List<Mono<Boolean>> tasks = new ArrayList<>();
                    if (!ObjectUtils.isEmpty(mediaMetadata.getImageUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaMetadata.getImageUrl(), IMAGE));
                    }
                    if (!ObjectUtils.isEmpty(mediaMetadata.getVideoUrl())) {
                        tasks.add(downloadContent(contentId, permalink, mediaMetadata.getVideoUrl(), VIDEO));
                    }
                    submitTasks(tasks, mediaMetadata, contentId, permalink);
                }
            } else {
                log.warn("Warning in InstagramParser.processSoup: Failed to process contentId={} url={} " +
                        "because media metadata is not found",
                        contentId,
                        url);
            }
        }

    }

    private MediaMetadata parseFromMediaResponse(String soup, WebClient client, String contentId, String url) {
        String pattern = "\\{\"media_id\"\\s*:\"[0-9a-zA-Z_]+\"}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(soup);
        JsonNode node = null;
        String mediaId = null;
        try {
            if (m.find()) {
                node = objectMapper.readValue(m.group(0), JsonNode.class);
            }
            if (node != null) {
                mediaId = node.get("media_id").asText().replace("\"", "").strip();
                String mediaUrl = String.format("https://i.instagram.com/api/v1/media/%s/info/", mediaId);
                client.addRequestHeader("user-agent", "Instagram 219.0.0.12.117 Android");
//                client.addRequestHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
//                client.addRequestHeader("origin", "https://www.instagram.com");
//                client.addRequestHeader("referer", "https://www.instagram.com/");
//                String url = "https://i.instagram.com/api/v1/media/2864542173792361567/info/";
                var response = client.getPage(mediaUrl).getWebResponse().getContentAsString();
                var responseNode = objectMapper.readValue(response, JsonNode.class);


                JsonNode items = responseNode.get("items").get(0);
                String videoUrl = "", imageUrl = "", description = "";
                var iter = items.fields();
                String message = "Warning in InstagramParser.parseFromMediaResponse: Unable to parse {} contentId={}, url={}";
                while (iter.hasNext()) {
                    var item = iter.next();
                    if (item.getKey().toLowerCase().contains("image_version")) {
                        try {
                            imageUrl = item.getValue().get("candidates").get(0).get("url").asText();
                        } catch (NullPointerException e) {
                            log.warn(message, "imageUrl", contentId, url);
                        }
                    } else if (item.getKey().toLowerCase().contains("video_version")) {
                        try {
                            videoUrl = item.getValue().get(0).get("url").asText();
                        } catch (NullPointerException e) {
                            log.warn(message, "videoUrl", contentId, url);
                        }
                    } else if (item.getKey().equalsIgnoreCase("caption")) {
                        try {
                            description = item.getValue().get("text").asText();
                        } catch (NullPointerException e) {
                            log.warn(message, "description", contentId, url);
                        }
                    }
                }
                if (!ObjectUtils.isEmpty(videoUrl)) {
                    return new MediaMetadata(imageUrl, "", videoUrl, null, VIDEO, "video/mp4", description);
                }
            }
        } catch (Exception e) {
            log.error("Error in InstagramParser.parseFromMediaResponse: {}", e.getLocalizedMessage());
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
        if (soup.contains("LoginAndSignupPage") || soup.contains("not-logged-in")) {
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
        if (soup.contains("LoginAndSignupPage") || soup.contains("not-logged-in")) {
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


//    catch (Exception e) {
//        updateInvalidUrl(contentId, url);
//        log.error("InstagramParser.downloadMedia: {}", e.getLocalizedMessage());
//    }
//    }

//    private void downloadMedia(String url, String contentId, WebClient client) {
//        try {
////            Cookie cookie1 = new Cookie(".instagram.com", "sessionid", "53740021732%3ANFiZ2bC0V13s22%3A27%3AAYfCmniACAm-moADC_Eb8o7_2n29jRy-Rxo1lrjQpg");
//////            Cookie cookie2 = new Cookie("www.instagram.com", "csrftoken", "5Bz6gOgoPHyVNG77igBNYn51oyqePqlZ");
////            client.getCookieManager().addCookie(cookie1);
////            client.getCookieManager().addCookie(cookie2);
////            client.addRequestHeader("cookie", "sessionid=53740021732%3ANFiZ2bC0V13s22%3A27%3AAYfCmniACAm-moADC_Eb8o7_2n29jRy-Rxo1lrjQpg");
////            String mediaId = "2826712927323905908";
////            var infoResponse = new MediaInfoRequest(mediaId).execute(igClient).join();
////            var permalinkResponse = new MediaPermalinkRequest(mediaId).execute(igClient).join();
//
//
////            var response = igClient.getHttpClient().newCall(
////                    new Request.Builder()
////                            .url(url).
////                            build())
////                    .execute();
//
////            String encryptionId = "131", encryptionKey = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE5dVdOWjYrY2V3ODBrcXpCeEloWApHOEdFR2JlQmwxcVFhYnZwWHpCSjRRdjFlbTdwOHNaL3FjejFwS01xSlY1eExFSnRZOEl6L2lES2o0RXc5WWJJCnFCME80cWQwWXNiNXFIRTI1bys5Nko1K2dlQUFBQWhqWlU5K0NvZGZBR004aTZyTVRLYWJCaWpjbno1U1VHM0wKMkdLZ20rdThDMnNMcWpSV05NM1BPOE1aZE1TS1l3eWlyNFVTTHQreHNQQWlPaGhXSVQveHNNT0lQbnVONnljVApLdFhBVDAvWTlFQk05MG5WcG5jKytmbU1kRytUVzduMXF2QmN2VVRmTlJKNVhuRTlTREtrQUVkeEg5QU4wcjdRCmdLb0l4cnljRlZxdTdMcE0zNWdYRmZraDNYNnI4RDZQMU50RCtZTWpHNmlqWDJUMDM3eDhtMGcrS3FZTFNwZDMKQVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==", authorization;
////            String username = "tessa.watler";
////            String password = "BeenThereDoneThat2";
////            String encPassword = IGUtils.encryptPassword(password, encryptionId, encryptionKey);
////            String loginUrl = "https://www.instagram.com/accounts/login/ajax";
////            WebRequest request = new WebRequest(new URL(loginUrl));
////            Request req = new Request.Builder().url(loginUrl).build();
////            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
////            Request request = req.newBuilder()
////                    .header("User-Agent", "Your-App-Name")
////                    .header("Accept", "application/vnd.yourapi.v1.full+json")
////                    .method("POST", RequestB)
////                    .build();
//
//
//
////            OkHttpClient okHttpClient = new OkHttpClient.Builder().
////                    Headers headerbuild = Headers.of(header);
////            Request request = new Request.Builder().url(url).headers(headerbuild).
////                    build();
//
//
//            //OkHttpClient.Builder()
//
//
//
//
//
//
//
//
//
//
////            HtmlPage page = client.getPage(url);
//            String body = page.getWebResponse().getContentAsString().replace("\\", "");
//            Document doc = Jsoup.parse(page.getWebResponse().getContentAsString().replace("\\", ""));
//            Map<String, String> metadata = parseMetadata(doc);
//
//
//
//            String json = null;
//            List<DataNode> dataNodes = doc.select("script").dataNodes();
//            for (DataNode dataNode : dataNodes) {
//                String wholeData = dataNode.getWholeData();
//                if (wholeData.startsWith("window._sharedData")) {
//                    json = wholeData.replace("window._sharedData =", "").replace("\n", "").strip();
//                    break;
//                }
//            }
//            JsonNode media = null;
//            if (json != null) {
//                JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
//                media = jsonNode
//                        .get("entry_data")
//                        .get("PostPage")
//                        .get(0)
//                        .get("graphql")
//                        .get("shortcode_media");
//                JsonNode imageUrlNode = media
//                        .get("display_url");
//                JsonNode videoUrlNode = media
//                        .get("video_url");
//                String description = "";
//                try {
//                    JsonNode captionNode = media
//                            .get("edge_media_to_caption")
//                            .get("edges");
//                    if (captionNode.size() > 0) {
//                        description = captionNode.get(0)
//                                .get("node")
//                                .get("text").asText();
//                    }
//                } catch (Exception e) {
//                    log.error("InstagramParser.downloadMedia: {}", e.getLocalizedMessage());
//                }
//
//                if (videoUrlNode != null) {
//                    String videoUrl = videoUrlNode.asText();
//                    String imageUrl = "";
//                    if (imageUrlNode != null) {
//                        imageUrl = imageUrlNode.asText();
//                    }
//                    String permalink = metadata.getOrDefault("permalink", url);
//                    uniquePermalinks.put(permalink, true);
//                    MediaUrl mediaUrl = new MediaUrl(imageUrl, "", videoUrl, null, VIDEO, "video/mp4");
//                    updateMetadata(contentId, metadata.getOrDefault("title", description),
//                            permalink, mediaUrl);
//                    List<Mono<Boolean>> tasks = new ArrayList<>();
//                    if (!ObjectUtils.isEmpty(mediaUrl.getImageUrl())) {
//                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getImageUrl(), IMAGE));
//                    }
//                    if (!ObjectUtils.isEmpty(mediaUrl.getVideoUrl())) {
//                        tasks.add(downloadContent(contentId, permalink, mediaUrl.getVideoUrl(), VIDEO));
//                    }
//                    submitTasks(tasks, mediaUrl, contentId, permalink);
//                }
//            } else {
//                log.warn("Parsable sharedData object not found for {}", contentId);
//            }
//        } catch (Exception e) {
//            updateInvalidUrl(contentId, url);
//            log.error("InstagramParser.downloadMedia: {}", e.getLocalizedMessage());
//        }
//
//
//    }
}
