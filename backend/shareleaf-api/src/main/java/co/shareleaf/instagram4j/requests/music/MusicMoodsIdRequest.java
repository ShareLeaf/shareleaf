package co.shareleaf.instagram4j.requests.music;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.requests.music.MusicSearchRequest.MusicQueryPayload;
import co.shareleaf.instagram4j.responses.music.MusicTrackResponse;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class MusicMoodsIdRequest extends IGPostRequest<MusicTrackResponse> {
    @NonNull
    private String _id;
    private String _cursor = "0";

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new MusicQueryPayload(client.getSessionId(), _cursor);
    }

    @Override
    public String path() {
        return "music/moods/" + _id + "/";
    }

    @Override
    public Class<MusicTrackResponse> getResponseType() {
        return MusicTrackResponse.class;
    }

}
