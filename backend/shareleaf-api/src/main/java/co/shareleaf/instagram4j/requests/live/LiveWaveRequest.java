package co.shareleaf.instagram4j.requests.live;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveWaveRequest extends IGPostRequest<IGResponse> {
    @NonNull
    private String broadcast_id, _viewer_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String viewer_id = _viewer_id;
        };
    }

    @Override
    public String path() {
        return "live/" + broadcast_id + "/wave/";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

}
