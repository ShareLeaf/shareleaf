package co.shareleaf.instagram4j.responses.direct;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class DirectGetPresenceResponse extends IGResponse {
    private Map<Long, UserPresence> user_presence;

    @Data
    public static class UserPresence {
        @JsonProperty("is_active")
        private boolean is_active;
        private long last_activity_at_ms;
    }
}
