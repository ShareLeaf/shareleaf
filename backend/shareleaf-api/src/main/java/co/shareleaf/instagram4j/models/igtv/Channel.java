package co.shareleaf.instagram4j.models.igtv;

import java.util.List;

import co.shareleaf.instagram4j.models.IGBaseModel;
import co.shareleaf.instagram4j.models.media.timeline.TimelineVideoMedia;
import co.shareleaf.instagram4j.models.user.User;

import lombok.Data;

@Data
public class Channel extends IGBaseModel {
    private String id;
    private List<TimelineVideoMedia> items;
    private boolean more_available;
    private String title;
    private String type;
    private String max_id;
    private User user_dict;
    private String description;
    private String cover_photo_url;
}
