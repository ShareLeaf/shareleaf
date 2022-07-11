package co.shareleaf.instagram4j.responses.media;

import co.shareleaf.instagram4j.models.media.reel.VoterInfo;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class MediaGetStoryPollVotersResponse extends IGResponse implements IGPaginatedResponse {
    private VoterInfo voter_info;

    @Override
    public String getNext_max_id() {
        return voter_info.getMax_id();
    }

    @Override
    public boolean isMore_available() {
        return voter_info.isMore_available();
    }

}
