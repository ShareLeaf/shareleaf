package co.shareleaf.instagram4j.responses.media;

import co.shareleaf.instagram4j.models.media.Media;
import co.shareleaf.instagram4j.models.media.reel.ReelMedia;
import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.models.media.timeline.TimelineVideoMedia;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class MediaResponse extends IGResponse {
    private Media media;

    @Data
    public static class MediaConfigureTimelineResponse extends MediaResponse {
        private TimelineMedia media;
    }

    @Data
    public static class MediaConfigureSidecarResponse extends MediaConfigureTimelineResponse {
        private String client_sidecar_id;
    }

    @Data
    public static class MediaConfigureToStoryResponse extends MediaResponse {
        private ReelMedia media;
    }

    @Data
    public static class MediaConfigureToIgtvResponse extends MediaResponse {
        private TimelineVideoMedia media;
    }
}
