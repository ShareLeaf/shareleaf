package co.shareleaf.service.scraper;

import co.shareleaf.props.InstagramProps;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Bizuwork Melesse
 * created on 6/22/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperUtils {
    private final ErrorListener errorListener;
    private final InstagramProps instagramProps;

    public WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(60000);
        webClient.setJavaScriptErrorListener(errorListener.javaScriptErrorListener());

        String sessionName = "sessionid";
        webClient.getCookieManager().addCookie(new Cookie("www.instagram.com", sessionName, instagramProps.getSessionId()));
        webClient.getCookieManager().addCookie(new Cookie("i.instagram.com", sessionName, instagramProps.getSessionId()));
        webClient.addRequestHeader("user-agent", instagramProps.getIosUserAgent());
        webClient.addRequestHeader("pragma", "no-cache");
        webClient.addRequestHeader("cache-control", "no-cache");
        return webClient;
    }
}
