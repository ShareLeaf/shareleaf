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
public class FeedTimelineResponse extends IGResponse implements IGPaginatedResponse {
    private boolean auto_load_more_enabled;
    private int num_results;
    private String next_max_id;
    @JsonDeserialize(converter = FilterToIGTimelineMedia.class)
    private List<TimelineMedia> feed_items;
    private boolean more_available;

    private static class FilterToIGTimelineMedia
            extends StdConverter<List<Map<String, Object>>, List<TimelineMedia>> {
        @Override
        public List<TimelineMedia> convert(List<Map<String, Object>> value) {
            return value.stream()
                    .filter(m -> m.containsKey("media_or_ad"))
                    .map(m -> IGUtils.convertToView(m.get("media_or_ad"), TimelineMedia.class))
                    .collect(Collectors.toList());
        }
    }
}
