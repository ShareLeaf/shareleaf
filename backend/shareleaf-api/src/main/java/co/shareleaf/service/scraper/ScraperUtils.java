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
        webClient.addRequestHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
        webClient.addRequestHeader("pragma", "no-cache");
        webClient.addRequestHeader("cache-control", "no-cache");
        webClient.addRequestHeader("sec-ch-ua-platform", "macOS");
        webClient.setJavaScriptErrorListener(errorListener.javaScriptErrorListener());
        return webClient;
    }
}
