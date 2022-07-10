package co.shareleaf.instagram4j.responses.direct;

import co.shareleaf.instagram4j.models.direct.Inbox;
import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class DirectInboxResponse extends IGResponse {
    private User viewer;
    private Inbox inbox;
    private int seq_id;
    private int pending_requests_total;
    private User most_recent_inviter;
}
