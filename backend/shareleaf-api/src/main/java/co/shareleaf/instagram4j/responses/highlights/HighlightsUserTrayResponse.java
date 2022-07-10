package co.shareleaf.instagram4j.responses.highlights;

import java.util.List;

import co.shareleaf.instagram4j.models.highlights.Highlight;
import co.shareleaf.instagram4j.models.igtv.Channel;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class HighlightsUserTrayResponse extends IGResponse {
    private List<Highlight> tray;
    private Channel tv_channel;
}
