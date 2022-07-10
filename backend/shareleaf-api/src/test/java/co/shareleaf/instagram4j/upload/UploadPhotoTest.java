package co.shareleaf.instagram4j.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.exceptions.IGLoginException;
import co.shareleaf.instagram4j.exceptions.IGResponseException;
import co.shareleaf.instagram4j.models.location.Location.Venue;
import co.shareleaf.instagram4j.models.media.UserTags.UserTagPayload;
import co.shareleaf.instagram4j.requests.IGRequest;
import co.shareleaf.instagram4j.requests.locationsearch.LocationSearchRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureTimelineRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureTimelineRequest.MediaConfigurePayload;
import co.shareleaf.instagram4j.requests.upload.RuploadPhotoRequest;
import co.shareleaf.instagram4j.responses.media.MediaResponse.MediaConfigureTimelineResponse;
import co.shareleaf.instagram4j.responses.media.RuploadPhotoResponse;

import serialize.SerializeTestUtil;

public class UploadPhotoTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void uploadTest()
            throws IGLoginException, IOException, IGResponseException, ClassNotFoundException {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        File file = new File("src/examples/resources/4to5ratio.jpg");
        byte[] imgData = Files.readAllBytes(file.toPath());
        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
        Venue location = new LocationSearchRequest(0d, 0d, "london bridge").execute(client).join()
                .getVenues().get(0);
        String id = client.sendRequest(uploadReq).join().getUpload_id();
        IGRequest<MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
                new MediaConfigurePayload().upload_id(id).caption("This is test").location(location)
                        .usertags(new UserTagPayload(18428658l, 0.5, 0.5)));
        MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();

        Assert.assertEquals("ok", response.getStatus());
    }
}
