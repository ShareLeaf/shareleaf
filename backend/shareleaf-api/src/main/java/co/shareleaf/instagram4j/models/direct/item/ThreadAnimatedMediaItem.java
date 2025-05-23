package co.shareleaf.instagram4j.models.direct.item;

import com.fasterxml.jackson.annotation.JsonTypeName;
import co.shareleaf.instagram4j.models.media.thread.ThreadAnimatedMedia;

import lombok.Data;

@Data
@JsonTypeName("animated_media")
public class ThreadAnimatedMediaItem extends ThreadItem {
    private ThreadAnimatedMedia media;
}
