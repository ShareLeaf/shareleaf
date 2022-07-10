package co.shareleaf.instagram4j.live;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastCommentRequest;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastGetCommentRequest;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastGetViewerListRequest;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastHeartbeatRequest;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastLikeRequest;
import co.shareleaf.instagram4j.requests.live.LiveBroadcastQuestionsRequest;
import co.shareleaf.instagram4j.requests.live.LiveWaveRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class LiveActionsTest {
    private static final String BROADCAST_ID = "17845796883218625";

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testComment() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastCommentRequest IGRequest =
                new LiveBroadcastCommentRequest(BROADCAST_ID, "Test");

        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("Success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testHeartbeat() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastHeartbeatRequest IGRequest = new LiveBroadcastHeartbeatRequest(BROADCAST_ID);
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testGetComment() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastGetCommentRequest IGRequest = new LiveBroadcastGetCommentRequest(BROADCAST_ID);
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testLike() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastLikeRequest IGRequest = new LiveBroadcastLikeRequest(BROADCAST_ID, 69);
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testGetViewerList() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastGetViewerListRequest IGRequest =
                new LiveBroadcastGetViewerListRequest(BROADCAST_ID);
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testBroadcastQuestions() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveBroadcastQuestionsRequest IGRequest =
                new LiveBroadcastQuestionsRequest(BROADCAST_ID, "What is the meaning of life?");
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testWave() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        LiveWaveRequest IGRequest = new LiveWaveRequest(BROADCAST_ID, "");
        IGResponse response = IGRequest.execute(client).join();

        Assert.assertEquals("ok", response.getStatus());
        log.debug("success");
    }
}
