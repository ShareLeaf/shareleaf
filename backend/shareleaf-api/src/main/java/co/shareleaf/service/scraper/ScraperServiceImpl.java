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
        try { // TODO: check that the content can be processed before scraping
            Platform platform = getPlatform(url);
            WebClient client = scraperUtils.getWebClient();
            log.info("About to process URL for {} with content ID {}: {}", platform.name(), contentId, url);
            HtmlPage page = client.getPage(url);
            int statusCode = page.getWebResponse().getStatusCode();
            if (statusCode >= 200 && statusCode < 400 ) {
                String soup = page.getWebResponse().getContentAsString();
                switch (platform) {
                    case REDDIT:
                        redditParser.processSoup(soup, url, contentId, client);
                        break;
                    case INSTAGRAM:
                        instagramParser.processSoup(soup, url, contentId, client);
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
            if (parsedUrl.getHost().toLowerCase().contains("reddit")) {
                return Platform.REDDIT;
            }
            else if (parsedUrl.getHost().toLowerCase().contains("instagram")) {
                return Platform.INSTAGRAM;
            }
        }
        return Platform.INSTAGRAM;
    }
}
