package co.shareleaf.service.scraper;

import co.shareleaf.service.parser.InstagramParser;
import co.shareleaf.service.parser.RedditParser;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Bizuwork Melesse
 * created on 6/17/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {
    private final RedditParser redditParser;
    private final InstagramParser instagramParser;
    private final ScraperUtils scraperUtils;

    @Override
    public boolean getContent(String contentId, String url) {
        instagramParser.processSoup(null, url, contentId, null); // TODO: remove after testing
        try { // TODO: check that the content can be processed before scraping
            WebClient client = scraperUtils.getWebClient(Platform.REDDIT);
            HtmlPage page = client.getPage(url);
            int statusCode = page.getWebResponse().getStatusCode();
            if (statusCode >= 200 && statusCode < 400 ) {
                Platform platform = getPlatform(url);
                switch (platform) {
                    case REDDIT:
                        log.info("About to process URL for Reddit with content ID {}: {}", contentId, url);
                        redditParser.processSoup(page.getWebResponse().getContentAsString(), url, contentId, client);
                        break;
                    case INSTAGRAM:
                        log.info("About to process URL for Instagram with content ID {}: {}", contentId, url);
                        instagramParser.processSoup(page.getWebResponse().getContentAsString(), url, contentId, client);
                        break;
                    default:
                        break;
                }
            }
            return true;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
        return false;
    }

    private Platform getPlatform(String url) {
        URL parsedUrl = null;
        try {
            parsedUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (parsedUrl != null) {
            if (parsedUrl.getHost().toLowerCase().contains("reddit.com")) {
                return Platform.REDDIT;
            }
            else if (parsedUrl.getHost().toLowerCase().contains("instagram.com")) {
                return Platform.INSTAGRAM;
            }
        }
        return Platform.INSTAGRAM;
    }
}
