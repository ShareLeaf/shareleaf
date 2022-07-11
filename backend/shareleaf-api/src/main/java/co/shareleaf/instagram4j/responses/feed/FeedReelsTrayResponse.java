package co.shareleaf.instagram4j.responses.feed;

import java.util.List;

import co.shareleaf.instagram4j.models.feed.Reel;
import co.shareleaf.instagram4j.models.live.Broadcast;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FeedReelsTrayResponse extends IGResponse {
    private List<Reel> tray;
    private List<Broadcast> broadcasts;
}
