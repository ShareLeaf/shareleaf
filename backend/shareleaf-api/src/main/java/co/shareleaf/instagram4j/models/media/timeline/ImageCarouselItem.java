package co.shareleaf.instagram4j.models.media.timeline;

import com.fasterxml.jackson.annotation.JsonTypeName;
import co.shareleaf.instagram4j.models.media.ImageVersions;

import lombok.Data;

// media_type 1
@Data
@JsonTypeName("1")
public class ImageCarouselItem extends CarouselItem {
    private ImageVersions image_versions2;
}
