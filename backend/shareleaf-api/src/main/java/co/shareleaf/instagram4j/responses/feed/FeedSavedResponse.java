package co.shareleaf.instagram4j.responses.feed;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import co.shareleaf.instagram4j.utils.IGUtils;
import lombok.Data;

@Data
public class FeedSavedResponse extends IGResponse implements IGPaginatedResponse {
    @JsonDeserialize(converter = MediaBeanToIGTimelineMedia.class)
    private List<TimelineMedia> items;
    private String next_max_id;
    private boolean more_available;

    private static class MediaBeanToIGTimelineMedia
            extends StdConverter<List<Map<String, Object>>, List<TimelineMedia>> {
        @Override
        public List<TimelineMedia> convert(List<Map<String, Object>> value) {
            return value.stream()
                    .filter(m -> m.containsKey("media"))
                    .map(m -> IGUtils.convertToView(m.get("media"), TimelineMedia.class))
                    .collect(Collectors.toList());
        }
    }
}
