package co.shareleaf.instagram4j.responses.direct;

import co.shareleaf.instagram4j.models.direct.IGThread;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class DirectThreadsResponse extends IGResponse {
    private IGThread thread;
}
