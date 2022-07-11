package co.shareleaf.instagram4j.responses.usertags;

import java.util.List;

import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class UserTagsFeedResponse extends IGResponse implements IGPaginatedResponse{

	private List<TimelineMedia> items;
	
	private int num_results;
	private String next_max_id;
	private boolean more_available;
	
	
}
