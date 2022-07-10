package co.shareleaf.instagram4j.responses.live;

import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class LiveStartResponse extends IGResponse {
    private String media_id;
}
