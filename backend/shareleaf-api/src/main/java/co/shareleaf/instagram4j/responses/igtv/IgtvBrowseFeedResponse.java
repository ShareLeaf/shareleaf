package co.shareleaf.instagram4j.responses.igtv;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import co.shareleaf.instagram4j.models.igtv.Channel;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class IgtvBrowseFeedResponse extends IGResponse implements IGPaginatedResponse {
    private Channel my_channel;
    private List<Channel> channels;
    @JsonProperty("max_id")
    private String next_max_id;
    private boolean more_available;
}
