package co.shareleaf.instagram4j.responses.live;

import co.shareleaf.instagram4j.models.media.timeline.Comment;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class LiveBroadcastCommentResponse extends IGResponse {
    private Comment comment;
}
