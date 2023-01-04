package co.shareleaf.service.parser;

import co.shareleaf.dto.VideoInfoDto;
import com.gargoylesoftware.htmlunit.WebClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
public interface ParserService {

    /**
     * Keep track of unique content URLs that are being processed within the same request
     * session. If link that is currently being processed gets removed from this
     * map, then downstream processes will abort.
     */
    ConcurrentHashMap<String, Boolean> uniquePermalinks = new ConcurrentHashMap<>();

    /**
     * Parse HTML soup and generate structured data for
     * downstream consumption
     * @param soup html soup
     * @param url
     * @param contentId
     * @param client
     */
    void processSoup(String soup, String url, String contentId, WebClient client);

    void processUrlV2(String igUrl, String contentId);
}
