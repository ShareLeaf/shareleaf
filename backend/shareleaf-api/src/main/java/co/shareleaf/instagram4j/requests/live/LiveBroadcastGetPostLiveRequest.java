package co.shareleaf.instagram4j.requests.live;

import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveBroadcastGetPostLiveRequest extends IGGetRequest<IGResponse> {
    @NonNull
    private String _broadcast_string;

    @Override
    public String path() {
        return "live/" + _broadcast_string + "/get_post_live/";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

}
