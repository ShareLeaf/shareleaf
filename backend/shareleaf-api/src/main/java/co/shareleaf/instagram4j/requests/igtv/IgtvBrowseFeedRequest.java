package co.shareleaf.instagram4j.requests.igtv;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.requests.IGPaginatedRequest;
import co.shareleaf.instagram4j.responses.igtv.IgtvBrowseFeedResponse;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class IgtvBrowseFeedRequest extends IGGetRequest<IgtvBrowseFeedResponse>
        implements IGPaginatedRequest {
    @Setter
    private String max_id;

    @Override
    public String path() {
        return "igtv/browse_feed/";
    }

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("max_id", max_id);
    }

    @Override
    public Class<IgtvBrowseFeedResponse> getResponseType() {
        return IgtvBrowseFeedResponse.class;
    }

}
