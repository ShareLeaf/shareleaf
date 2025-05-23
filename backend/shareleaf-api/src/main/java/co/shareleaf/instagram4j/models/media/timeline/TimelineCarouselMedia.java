package co.shareleaf.instagram4j.models.media.timeline;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;

@Data
@JsonTypeName("8")
public class TimelineCarouselMedia extends TimelineMedia {
    private int carousel_media_count;
    private List<CarouselItem> carousel_media;
}
