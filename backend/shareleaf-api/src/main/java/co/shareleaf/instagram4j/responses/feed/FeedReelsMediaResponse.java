package co.shareleaf.instagram4j.responses.feed;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import co.shareleaf.instagram4j.models.feed.Reel;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FeedReelsMediaResponse extends IGResponse {
    @JsonUnwrapped
    private Map<String, Reel> reels = new HashMap<String, Reel>();
}
