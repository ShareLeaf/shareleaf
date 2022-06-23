package co.shareleaf.service.scraper;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Bizuwork Melesse
 * created on 6/22/22
 */
@Slf4j
@Service
public record ScraperUtils(ErrorListener errorListener) {
    public WebClient getWebClient(Platform platform) {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(5000);
        webClient.setJavaScriptErrorListener(errorListener.javaScriptErrorListener());
        return webClient;
    }
}
