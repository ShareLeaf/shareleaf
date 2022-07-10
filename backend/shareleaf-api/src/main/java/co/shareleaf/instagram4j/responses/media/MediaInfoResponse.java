package co.shareleaf.instagram4j.responses.media;

import java.util.List;

import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class MediaInfoResponse extends IGResponse {
    private List<TimelineMedia> items;
    private int num_results;
    private boolean more_available;
}
