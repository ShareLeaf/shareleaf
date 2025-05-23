package co.shareleaf.service.scraper;

/**
 * @author Bizuwork Melesse
 * created on 6/17/22
 */
public interface ScraperService {

    /**
     * Get the media content at the given URL.
     * Use WebClient to simulate a full browser and execute
     * any JavaScript code. Use JSoup for all other downstream
     * tasks such as the parsing of the HTML content and the
     * downloading of the media content from the source.
     *
     * @param contentId
     * @param url
     * @return
     */
    boolean getContent(String contentId, String url);
}
