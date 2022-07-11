package co.shareleaf.instagram4j.requests.live;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.live.LiveBroadcastCommentResponse;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveBroadcastCommentRequest extends IGPostRequest<LiveBroadcastCommentResponse> {
    @NonNull
    private String broadcast_id, _message;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new LiveCommentPayload();
    }

    @Override
    public String path() {
        return "live/" + broadcast_id + "/comment/";
    }

    @Override
    public Class<LiveBroadcastCommentResponse> getResponseType() {
        return LiveBroadcastCommentResponse.class;
    }

    @Data
    public class LiveCommentPayload extends IGPayload {
        private String comment_text = _message;
    }

}
