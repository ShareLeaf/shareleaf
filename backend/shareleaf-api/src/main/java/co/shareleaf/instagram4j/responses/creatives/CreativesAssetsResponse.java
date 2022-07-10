package co.shareleaf.instagram4j.responses.creatives;

import java.util.List;

import co.shareleaf.instagram4j.models.creatives.StaticSticker;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class CreativesAssetsResponse extends IGResponse {
    private List<StaticSticker> static_stickers;
}
