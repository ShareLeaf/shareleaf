package co.shareleaf.instagram4j.requests.igtv;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPaginatedRequest;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.igtv.IgtvChannelResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
public class IgtvChannelRequest extends IGPostRequest<IgtvChannelResponse>
        implements IGPaginatedRequest {
    @NonNull
    private String _id;
    @Setter
    private String max_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IgtvChannelPayload();
    }

    @Override
    public String path() {
        return "igtv/channel/";
    }

    @Override
    public Class<IgtvChannelResponse> getResponseType() {
        return IgtvChannelResponse.class;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    public class IgtvChannelPayload extends IGPayload {
        private String id = _id;
        @JsonProperty("max_id")
        private String _max_id = max_id;
    }

}
