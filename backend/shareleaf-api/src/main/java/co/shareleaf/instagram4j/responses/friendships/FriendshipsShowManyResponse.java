package co.shareleaf.instagram4j.responses.friendships;

import java.util.Map;

import co.shareleaf.instagram4j.models.friendships.Friendship;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FriendshipsShowManyResponse extends IGResponse {
    private Map<String, Friendship> friendship_statuses;
}
