package co.shareleaf.instagram4j.responses.feed;

import java.util.List;
import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class FeedUserResponse extends IGResponse implements IGPaginatedResponse {
    private List<TimelineMedia> items;
    private String next_max_id;
    private int num_results;

    public boolean isMore_available() {
        return next_max_id != null;
    }
}
