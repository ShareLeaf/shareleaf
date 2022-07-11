package co.shareleaf.instagram4j.models.direct.item;

import com.fasterxml.jackson.annotation.JsonTypeName;
import co.shareleaf.instagram4j.models.media.Link;

import lombok.Data;

@Data
@JsonTypeName("link")
public class ThreadLinkItem extends ThreadItem {
    private Link link;
}
