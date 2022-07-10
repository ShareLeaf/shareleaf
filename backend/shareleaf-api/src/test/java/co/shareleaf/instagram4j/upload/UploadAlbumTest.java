package co.shareleaf.instagram4j.upload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import co.shareleaf.instagram4j.serialize.SerializeTestUtil;
import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.exceptions.IGLoginException;
import co.shareleaf.instagram4j.exceptions.IGResponseException;
import co.shareleaf.instagram4j.models.location.Location.Venue;
import co.shareleaf.instagram4j.models.media.UploadParameters;
import co.shareleaf.instagram4j.models.media.UserTags.UserTagPayload;
import co.shareleaf.instagram4j.requests.IGRequest;
import co.shareleaf.instagram4j.requests.locationsearch.LocationSearchRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureSidecarRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureSidecarRequest.MediaConfigureSidecarPayload;
import co.shareleaf.instagram4j.requests.media.MediaConfigureSidecarRequest.SidecarChildrenMetadata;
import co.shareleaf.instagram4j.requests.upload.RuploadPhotoRequest;
import co.shareleaf.instagram4j.requests.upload.RuploadVideoRequest;
import co.shareleaf.instagram4j.responses.IGResponse;
import co.shareleaf.instagram4j.responses.media.RuploadPhotoResponse;


public class UploadAlbumTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void uploadTest()
            throws IGLoginException, IOException, IGResponseException, ClassNotFoundException {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        File firstPhoto = new File("src/examples/resources/test.jpg"),
                secondVideo = new File("src/examples/resources/test.mp4"),
                thumb = new File("src/examples/resources/cover.jpg");
        byte[] firstImgData = Files.readAllBytes(firstPhoto.toPath()),
                scndVidData = Files.readAllBytes(secondVideo.toPath()),
                thumbData = Files.readAllBytes(thumb.toPath());
        BufferedImage firstImg = ImageIO.read(firstPhoto), thumbImg = ImageIO.read(thumb);
        // upload photo
        IGRequest<RuploadPhotoResponse> uploadFirstPhoto =
                new RuploadPhotoRequest(firstImgData, "1",
                        String.valueOf(System.currentTimeMillis()), true);
        String firstId = client.sendRequest(uploadFirstPhoto).join().getUpload_id();
        // upload video
        String uploadIdVid = System.currentTimeMillis() + "1";
        IGRequest<?> uploadSecondVideo = new RuploadVideoRequest(scndVidData,
                UploadParameters.forPhoto(uploadIdVid, "2", true, null));
        IGRequest<?> uploadThumbnail = new RuploadPhotoRequest(thumbData, "1", uploadIdVid, false);
        IGResponse vidRes = client.sendRequest(uploadSecondVideo).join(),
                thumbRes = client.sendRequest(uploadThumbnail).join();
        Venue location = new LocationSearchRequest(0d, 0d, "mcdonalds").execute(client).join()
                .getVenues().get(0);
        List<SidecarChildrenMetadata> metadata = Arrays.asList(
                new SidecarChildrenMetadata(firstId),
                new SidecarChildrenMetadata(uploadIdVid)
                        .usertags(new UserTagPayload(18428658l, 0.5, 0.5)));
        IGRequest<?> configReq = new MediaConfigureSidecarRequest(new MediaConfigureSidecarPayload()
                .caption("Wow").children_metadata(metadata).location(location));
        IGResponse response = client.sendRequest(configReq).join();

        Assert.assertEquals("ok", response.getStatus());
    }
}
