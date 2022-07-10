package co.shareleaf.instagram4j.direct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.exceptions.IGLoginException;
import co.shareleaf.instagram4j.exceptions.IGResponseException;
import co.shareleaf.instagram4j.models.media.UploadParameters;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsActionRequest;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsActionRequest.DirectThreadsAction;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastConfigurePhotoPayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastConfigureVideoPayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastLinkPayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastMediaSharePayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastProfilePayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastReelSharePayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastShareVoicePayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastStorySharePayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsBroadcastRequest.BroadcastTextPayload;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsMarkItemSeenRequest;
import co.shareleaf.instagram4j.requests.direct.DirectThreadsUpdateTitleRequest;
import co.shareleaf.instagram4j.requests.upload.MediaUploadFinishRequest;
import co.shareleaf.instagram4j.requests.upload.RuploadPhotoRequest;
import co.shareleaf.instagram4j.requests.upload.RuploadVideoRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import serialize.SerializeTestUtil;

public class DirectThreadBroadcastTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testSeen()
            throws IGLoginException, IGResponseException, IOException, ClassNotFoundException {
        IGClient lib = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse res = new DirectThreadsMarkItemSeenRequest("29390877766756105082649519748808704",
                thread_id).execute(lib).join();
        Assert.assertEquals("ok", res.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testText() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response = new DirectThreadsBroadcastRequest(
                new BroadcastTextPayload("Test https://google.com", thread_id)).execute(client)
                        .join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testProfile() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response =
                new DirectThreadsBroadcastRequest(new BroadcastProfilePayload("18428658", thread_id,
                        "340282366841710300949128134036896195180")).execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testMediaShare() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response = new DirectThreadsBroadcastRequest(
                new BroadcastMediaSharePayload("2336729204844097508_18428658", "test", thread_id,
                        "340282366841710300949128134036896195180")).execute(client).join(); // 2336729204844097508_18428658
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testReelShare() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response = new DirectThreadsBroadcastRequest(
                new BroadcastReelSharePayload("2343253215272024058_18428658", "test", thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testStoryShare() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response = new DirectThreadsBroadcastRequest(
                new BroadcastStorySharePayload("2343253215272024058_18428658", "test", thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testUploadPhoto() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        File photo = new File("src/examples/resources/cover.jpg");
        byte[] photoData = Files.readAllBytes(photo.toPath());
        String id = String.valueOf(System.currentTimeMillis());
        new RuploadPhotoRequest(photoData, "1", id, false).execute(client).join();
        IGResponse response =
                new DirectThreadsBroadcastRequest(new BroadcastConfigurePhotoPayload(id, thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testUploadVideo() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        File video = new File("src/examples/resources/test.mp4"),
                cover = new File("src/examples/resources/cover.jpg");
        String id = String.valueOf(System.currentTimeMillis());
        new RuploadVideoRequest(Files.readAllBytes(video.toPath()),
                UploadParameters.forDirectVideo(id)).execute(client).join();
        new RuploadPhotoRequest(Files.readAllBytes(cover.toPath()), "1").execute(client).join();
        new MediaUploadFinishRequest(id).execute(client).join();
        IGResponse response =
                new DirectThreadsBroadcastRequest(new BroadcastConfigureVideoPayload(id, thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testUploadVoice() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        String id = String.valueOf(System.currentTimeMillis());
        File voice = new File("src/examples/resources/01-replay.m4a");
        new RuploadVideoRequest(Files.readAllBytes(voice.toPath()),
                UploadParameters.forDirectVoice(id)).execute(client).join();
        new MediaUploadFinishRequest(id).execute(client).join();
        IGResponse response =
                new DirectThreadsBroadcastRequest(new BroadcastShareVoicePayload(id, thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testLink() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128163165245788989"; // "340282366841710300949128198037200507384";
        IGResponse response = new DirectThreadsBroadcastRequest(new BroadcastLinkPayload(
                "Test https://google.com", new String[] {"https://google.com"}, thread_id))
                        .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testChangeTitle() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128134036896195180";
        IGResponse response =
                new DirectThreadsUpdateTitleRequest(thread_id, "BOI").execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }

    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testAction() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        String thread_id = "340282366841710300949128134036896195180";
        IGResponse response = new DirectThreadsActionRequest(thread_id, DirectThreadsAction.UNMUTE)
                .execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
    }
}
