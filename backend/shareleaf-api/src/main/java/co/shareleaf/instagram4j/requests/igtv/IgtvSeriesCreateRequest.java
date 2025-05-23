package co.shareleaf.instagram4j.requests.igtv;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.igtv.IgtvSeriesResponse;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IgtvSeriesCreateRequest extends IGPostRequest<IgtvSeriesResponse> {
    @NonNull
    private String _title, _description;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IgtvSeriesCreatePayload();
    }

    @Override
    public String path() {
        return "igtv/series/create/";
    }

    @Override
    public Class<IgtvSeriesResponse> getResponseType() {
        return IgtvSeriesResponse.class;
    }

    @Data
    public class IgtvSeriesCreatePayload extends IGPayload {
        private String title = _title;
        private String description = _description;
    }

}
