package co.shareleaf.instagram4j.responses.feed;

import java.util.List;
import co.shareleaf.instagram4j.models.user.Profile;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class FeedUsersResponse extends IGResponse implements IGPaginatedResponse {
    private List<Profile> users;
    private String next_max_id;

    public boolean isMore_available() {
        return next_max_id != null;
    }
}
