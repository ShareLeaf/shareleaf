package co.shareleaf.instagram4j.responses.friendships;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import co.shareleaf.instagram4j.models.friendships.Friendship;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FriendshipsShowResponse extends IGResponse {
    @JsonUnwrapped
    private Friendship friendship;
}
