package co.shareleaf.instagram4j.responses.locationsearch;

import java.util.List;

import co.shareleaf.instagram4j.models.location.Location.Venue;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class LocationSearchResponse extends IGResponse {
    private List<Venue> venues;
    private String request_id;
    private String rank_token;
}
