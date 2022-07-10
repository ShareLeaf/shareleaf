package co.shareleaf.instagram4j.responses.friendships;

import co.shareleaf.instagram4j.models.friendships.Friendship;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FriendshipStatusResponse extends IGResponse {
    private Friendship friendship_status;
}
