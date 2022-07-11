package co.shareleaf.instagram4j.responses.media;

import co.shareleaf.instagram4j.models.media.reel.ResponderInfo;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class MediaGetStoryQuestionResponsesResponse extends IGResponse implements IGPaginatedResponse {
    private ResponderInfo responder_info;

    @Override
    public String getNext_max_id() {
        return this.responder_info.getMax_id();
    }

    @Override
    public boolean isMore_available() {
        return this.responder_info.isMore_available();
    }
}
