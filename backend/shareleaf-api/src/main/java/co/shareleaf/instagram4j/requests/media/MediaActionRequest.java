package co.shareleaf.instagram4j.requests.media;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MediaActionRequest extends IGPostRequest<IGResponse> {
    @NonNull
    private String _media_id;
    @NonNull
    private MediaAction action;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String media_id = _media_id;
        };
    }

    @Override
    public String path() {
        return String.format("media/%s/%s/", _media_id, action.name().toLowerCase());
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

    public static enum MediaAction {
        SAVE, UNSAVE, ONLY_ME, UNDO_ONLY_ME, DELETE, LIKE, UNLIKE, ENABLE_COMMENTS, DISABLE_COMMENTS;
    }

}
