package co.shareleaf.instagram4j.models.direct.item;

import com.fasterxml.jackson.annotation.JsonTypeName;
import co.shareleaf.instagram4j.models.media.Media;

import lombok.Data;

@Data
@JsonTypeName("media_share")
public class ThreadMediaShareItem extends ThreadItem {
    private Media media_share;
}
