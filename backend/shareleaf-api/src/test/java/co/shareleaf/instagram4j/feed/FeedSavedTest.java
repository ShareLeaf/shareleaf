package co.shareleaf.instagram4j.feed;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.feed.FeedSavedRequest;
import co.shareleaf.instagram4j.responses.feed.FeedSavedResponse;

import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class FeedSavedTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testName() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        FeedSavedResponse response = new FeedSavedRequest().execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
        response.getItems()
                .forEach(item -> log.debug("{} : {}", item.getId(), item.getClass().getName()));
        log.debug("Success");
    }
}
