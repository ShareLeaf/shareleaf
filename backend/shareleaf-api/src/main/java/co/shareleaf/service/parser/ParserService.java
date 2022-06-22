package co.shareleaf.service.parser;

/**
 * @author Bizuwork Melesse
 * created on 6/20/22
 */
public interface ParserService {

    /**
     * Parse HTML soup and generate structured data for
     * downstream consumption
     *  @param soup html soup
     * @param url
     * @param contentId
     */
    void processSoup(String soup, String url, String contentId);

    void uploadContent();
}
