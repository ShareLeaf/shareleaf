package co.shareleaf.instagram4j.media;

import org.junit.Assert;
import org.junit.Test;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.feed.FeedUserReelMediaRequest;
import co.shareleaf.instagram4j.requests.media.MediaActionRequest;
import co.shareleaf.instagram4j.requests.media.MediaActionRequest.MediaAction;
import co.shareleaf.instagram4j.requests.media.MediaCommentRequest;
import co.shareleaf.instagram4j.requests.media.MediaEditRequest;
import co.shareleaf.instagram4j.requests.media.MediaGetCommentsRequest;
import co.shareleaf.instagram4j.requests.media.MediaGetLikersRequest;
import co.shareleaf.instagram4j.requests.media.MediaInfoRequest;
import co.shareleaf.instagram4j.requests.media.MediaSeenRequest;
import co.shareleaf.instagram4j.responses.IGResponse;
import co.shareleaf.instagram4j.responses.feed.FeedUserReelsMediaResponse;
import co.shareleaf.instagram4j.responses.feed.FeedUsersResponse;
import co.shareleaf.instagram4j.responses.media.MediaCommentResponse;
import co.shareleaf.instagram4j.responses.media.MediaGetCommentsResponse;
import co.shareleaf.instagram4j.utils.IGUtils;
import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class MediaActionTestRequest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testInfo() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new MediaInfoRequest(IGUtils.fromCode("CClpx7qsYm-") + "").execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testDelete() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new MediaActionRequest("2342700818431830321_18428658", MediaAction.DELETE)
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testSave() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new MediaActionRequest("2351884457617569072_18428658", MediaAction.UNSAVE)
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testEdit() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response = new MediaEditRequest("2342700818431830321_18428658", "edited caption")
                .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testSeen() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        FeedUserReelsMediaResponse reels =
                new FeedUserReelMediaRequest(18428658l).execute(client).join();
        IGResponse response =
                new MediaSeenRequest(reels.getReel().getItems()).execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testOnlyMe() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new MediaActionRequest("2351884457617569072_18428658", MediaAction.UNDO_ONLY_ME)
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testLike() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response =
                new MediaActionRequest("2351884457617569072", MediaAction.ENABLE_COMMENTS)
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testComment() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        MediaCommentResponse response =
                new MediaCommentRequest("2351884457617569072", "Gangnam").execute(client).join();
        log.info(response.getComment().getPk());
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testComments() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        MediaGetCommentsResponse response =
                new MediaGetCommentsRequest("2355325964336133761_18428658").execute(client).join();
        response.getComments().forEach(c -> log.debug("{} : {}", c.getPk(), c.getText()));
        response = new MediaGetCommentsRequest("2355325964336133761_18428658",
                response.getNext_max_id()).execute(client).join();
        response.getComments().forEach(c -> log.debug("{} : {}", c.getPk(), c.getText()));
        Assert.assertEquals("ok", response.getStatus());
    }
    
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testLikers() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        FeedUsersResponse response =
                new MediaGetLikersRequest("2355325964336133761_18428658").execute(client).join();
        log.debug(response.getUsers().size() + "");
        Assert.assertEquals("ok", response.getStatus());
    }
}
