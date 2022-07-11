package co.shareleaf.instagram4j.requests.feed;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.requests.IGPaginatedRequest;
import co.shareleaf.instagram4j.responses.feed.FeedSavedResponse;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class FeedSavedRequest extends IGGetRequest<FeedSavedResponse>
        implements IGPaginatedRequest {
    @Setter
    private String max_id;

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("max_id", max_id);
    }

    @Override
    public String path() {
        return "feed/saved/";
    }

    @Override
    public Class<FeedSavedResponse> getResponseType() {
        return FeedSavedResponse.class;
    }

}
