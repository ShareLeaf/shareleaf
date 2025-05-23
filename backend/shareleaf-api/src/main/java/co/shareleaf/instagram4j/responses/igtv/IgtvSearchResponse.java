package co.shareleaf.instagram4j.responses.igtv;

import java.util.List;

import co.shareleaf.instagram4j.models.igtv.Channel;
import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class IgtvSearchResponse extends IGResponse {
    private List<IgtvSearchResult> results;
    private int num_results;
    private boolean has_more;
    private String rank_token;

    @Data
    public static class IgtvSearchResult {
        private String type;
        private User user;
        private Channel channel;
    }
}
