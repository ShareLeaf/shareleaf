package co.shareleaf.service.parser;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
public interface ParserService {

    /**
     * Parse HTML soup and generate structured data for
     * downstream consumption
     * @param soup html soup
     * @param url
     * @param contentId
     * @param client
     */
    void processSoup(String soup, String url, String contentId, WebClient client);
}
