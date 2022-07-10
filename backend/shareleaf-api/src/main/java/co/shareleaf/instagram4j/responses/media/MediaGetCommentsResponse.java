package co.shareleaf.instagram4j.responses.media;

import java.util.List;
import co.shareleaf.instagram4j.models.media.timeline.Comment;
import co.shareleaf.instagram4j.models.media.timeline.Comment.Caption;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class MediaGetCommentsResponse extends IGResponse implements IGPaginatedResponse {
    private List<Comment> comments;
    private Caption caption;
    private String next_max_id;

    public boolean isMore_available() {
        return next_max_id != null;
    }
}
