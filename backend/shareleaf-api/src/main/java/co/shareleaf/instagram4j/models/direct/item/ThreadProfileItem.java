package co.shareleaf.instagram4j.models.direct.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import co.shareleaf.instagram4j.models.media.timeline.TimelineMedia;
import co.shareleaf.instagram4j.models.user.Profile;

import lombok.Data;

@Data
@JsonTypeName("profile")
public class ThreadProfileItem extends ThreadItem {
    private Profile profile;
    private List<TimelineMedia> preview_medias;
}
