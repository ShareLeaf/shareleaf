package co.shareleaf.instagram4j.requests.friendships;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.friendships.FriendshipsShowManyResponse;

import lombok.Getter;
import lombok.NonNull;

public class FriendshipsShowManyRequest extends IGPostRequest<FriendshipsShowManyResponse> {
    @NonNull
    private String _user_ids;

    public FriendshipsShowManyRequest(Long... user_pks) {
        this._user_ids = Stream.of(user_pks).map(Object::toString).collect(Collectors.joining(","));
    }

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String user_ids = _user_ids;
        };
    }

    @Override
    public String path() {
        return "friendships/show_many/";
    }

    @Override
    public Class<FriendshipsShowManyResponse> getResponseType() {
        return FriendshipsShowManyResponse.class;
    }
}
