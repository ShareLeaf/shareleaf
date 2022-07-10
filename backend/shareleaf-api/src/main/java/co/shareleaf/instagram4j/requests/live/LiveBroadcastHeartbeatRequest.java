package co.shareleaf.instagram4j.requests.live;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.live.LiveBroadcastHeartbeatResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiveBroadcastHeartbeatRequest extends IGPostRequest<LiveBroadcastHeartbeatResponse> {
    @NonNull
    private String broadcast_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload();
    }

    @Override
    public String path() {
        return "live/" + broadcast_id + "/heartbeat_and_get_viewer_count/";
    }

    @Override
    public Class<LiveBroadcastHeartbeatResponse> getResponseType() {
        return LiveBroadcastHeartbeatResponse.class;
    }

}
