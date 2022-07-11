package co.shareleaf.instagram4j.responses.live;

import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class LiveCreateResponse extends IGResponse {
    private String broadcast_id;
    private String upload_url;

    public String getBroadcastUrl() {
        return upload_url.split(broadcast_id, 2)[0];
    }

    public String getBroadcastKey() {
        return broadcast_id + upload_url.split(broadcast_id, 2)[1];
    }
}
