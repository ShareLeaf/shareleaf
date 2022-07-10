package co.shareleaf.instagram4j.feed;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.feed.FeedReelsMediaRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class FeedReelsMediaTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testName() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new FeedReelsMediaRequest("archiveDay:18116528737123990").execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
        log.debug("Success");
    }
}
