package co.shareleaf.instagram4j.requests.media;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MediaCommentLikeRequest extends IGPostRequest<IGResponse> {
    @NonNull
    private String _comment_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String comment_id = _comment_id;
        };
    }

    @Override
    public String path() {
        return String.format("media/%s/comment_like/", _comment_id);
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

}