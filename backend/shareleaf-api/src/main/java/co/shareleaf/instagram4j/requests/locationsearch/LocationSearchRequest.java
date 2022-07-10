package co.shareleaf.instagram4j.requests.locationsearch;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.locationsearch.LocationSearchResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocationSearchRequest extends IGGetRequest<LocationSearchResponse> {
    @NonNull
    private Double longitude, latitude;
    @NonNull
    private String query;

    @Override
    public String path() {
        return "location_search/";
    }

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("longitude", longitude.toString(), "latitude", latitude.toString(),
                "search_query", query);
    }

    @Override
    public Class<LocationSearchResponse> getResponseType() {
        return LocationSearchResponse.class;
    }

}
