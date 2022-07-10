package co.shareleaf.instagram4j.responses.feed;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import co.shareleaf.instagram4j.models.feed.Reel;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class FeedUserReelsMediaResponse extends IGResponse {
    @JsonUnwrapped
    private Reel reel;
}
