package co.shareleaf.instagram4j.actions.media;

import java.util.concurrent.CompletableFuture;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.actions.feed.FeedIterable;
import co.shareleaf.instagram4j.requests.media.MediaActionRequest;
import co.shareleaf.instagram4j.requests.media.MediaCommentRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureSidecarRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureSidecarRequest.MediaConfigureSidecarPayload;
import co.shareleaf.instagram4j.requests.media.MediaConfigureTimelineRequest;
import co.shareleaf.instagram4j.requests.media.MediaConfigureTimelineRequest.MediaConfigurePayload;
import co.shareleaf.instagram4j.requests.media.MediaConfigureToIgtvRequest;
import co.shareleaf.instagram4j.requests.media.MediaEditRequest;
import co.shareleaf.instagram4j.requests.media.MediaGetCommentsRequest;
import co.shareleaf.instagram4j.requests.media.MediaInfoRequest;
import co.shareleaf.instagram4j.responses.IGResponse;
import co.shareleaf.instagram4j.responses.media.MediaCommentResponse;
import co.shareleaf.instagram4j.responses.media.MediaGetCommentsResponse;
import co.shareleaf.instagram4j.responses.media.MediaInfoResponse;
import co.shareleaf.instagram4j.responses.media.MediaResponse;
import co.shareleaf.instagram4j.responses.media.MediaResponse.MediaConfigureSidecarResponse;
import co.shareleaf.instagram4j.responses.media.MediaResponse.MediaConfigureTimelineResponse;
import co.shareleaf.instagram4j.responses.media.MediaResponse.MediaConfigureToIgtvResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MediaAction {
    @NonNull
    private IGClient client;
    @NonNull
    private String media_id;
    
    public CompletableFuture<MediaCommentResponse> comment(String comment) {
        return new MediaCommentRequest(media_id, comment).execute(client);
    }
    
    public CompletableFuture<MediaResponse> editCaption(String caption) {
        return new MediaEditRequest(media_id, caption).execute(client);
    }
    
    public CompletableFuture<MediaInfoResponse> info() {
        return new MediaInfoRequest(media_id).execute(client);
    }
    
    public FeedIterable<MediaGetCommentsRequest, MediaGetCommentsResponse> comments() {
        return new FeedIterable<>(client, () -> new MediaGetCommentsRequest(media_id));
    }
    
    public CompletableFuture<IGResponse> action(MediaActionRequest.MediaAction action) {
        return new MediaActionRequest(media_id, action).execute(client);
    }
    
    public static MediaAction of(IGClient client, String media_id) {
        return new MediaAction(client, media_id);
    }
    
    public static CompletableFuture<MediaConfigureTimelineResponse> configureMediaToTimeline(IGClient client, String upload_id, MediaConfigurePayload payload) {
        return new MediaConfigureTimelineRequest(payload.upload_id(upload_id)).execute(client);
    }
    
    public static CompletableFuture<MediaConfigureSidecarResponse> configureAlbumToTimeline(IGClient client, MediaConfigureSidecarPayload payload) {
        return new MediaConfigureSidecarRequest(payload).execute(client);
    }
    
    public static CompletableFuture<MediaConfigureToIgtvResponse> configureToIgtv(IGClient client, String upload_id, String title, String caption, boolean postToFeed) {
        return new MediaConfigureToIgtvRequest(upload_id, title, caption, postToFeed).execute(client);
    }
}
