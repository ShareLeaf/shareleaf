package co.shareleaf.instagram4j.responses.media;

import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class MediaPermalinkResponse extends IGResponse {
    private String permalink;
}
