package co.shareleaf.instagram4j.responses.feed;

import co.shareleaf.instagram4j.models.feed.Reel;
import co.shareleaf.instagram4j.models.live.Broadcast;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FeedUserStoryResponse extends IGResponse {
    private Reel reel;
    private Broadcast broadcast;
}
