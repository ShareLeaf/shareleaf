package co.shareleaf.instagram4j.requests.music;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.music.MusicBrowseResponse;

import lombok.Getter;

public class MusicBrowseRequest extends IGPostRequest<MusicBrowseResponse> {

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String session_id = client.getSessionId();
        };
    }

    @Override
    public String path() {
        return "music/browse/";
    }

    @Override
    public Class<MusicBrowseResponse> getResponseType() {
        return MusicBrowseResponse.class;
    }

}
