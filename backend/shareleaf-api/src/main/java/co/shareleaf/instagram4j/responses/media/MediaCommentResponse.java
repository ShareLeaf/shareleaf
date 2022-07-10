package co.shareleaf.instagram4j.responses.media;

import co.shareleaf.instagram4j.models.media.timeline.Comment;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class MediaCommentResponse extends IGResponse {
    private Comment comment;
}
