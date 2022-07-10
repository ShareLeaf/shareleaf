package co.shareleaf.instagram4j.responses.highlights;

import co.shareleaf.instagram4j.models.feed.Reel;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class HighlightsCreateReelResponse extends IGResponse {
    private Reel reel;
}
