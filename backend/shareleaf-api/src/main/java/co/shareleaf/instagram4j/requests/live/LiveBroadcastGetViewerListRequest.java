package co.shareleaf.instagram4j.requests.live;

import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.live.LiveBroadcastGetViewerListResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveBroadcastGetViewerListRequest
        extends IGGetRequest<LiveBroadcastGetViewerListResponse> {
    @NonNull
    private String broadcast_id;

    @Override
    public String path() {
        return "live/" + broadcast_id + "/get_viewer_list/";
    }

    @Override
    public Class<LiveBroadcastGetViewerListResponse> getResponseType() {
        return LiveBroadcastGetViewerListResponse.class;
    }

}
