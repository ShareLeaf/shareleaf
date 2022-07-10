package co.shareleaf.instagram4j.responses.live;

import java.util.List;

import co.shareleaf.instagram4j.models.user.Profile;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class LiveBroadcastGetViewerListResponse extends IGResponse {
    private List<Profile> users;
}
