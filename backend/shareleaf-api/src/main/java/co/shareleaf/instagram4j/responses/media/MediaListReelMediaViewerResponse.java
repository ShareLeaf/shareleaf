package co.shareleaf.instagram4j.responses.media;

import java.util.List;
import co.shareleaf.instagram4j.models.media.reel.ReelMedia;
import co.shareleaf.instagram4j.models.user.Profile;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class MediaListReelMediaViewerResponse extends IGResponse implements IGPaginatedResponse {
    private List<Profile> users;
    private String next_max_id;
    private int user_count;
    private int total_viewer_count;
    private ReelMedia updated_media;

    public boolean isMore_available() {
        return next_max_id != null;
    }
}
