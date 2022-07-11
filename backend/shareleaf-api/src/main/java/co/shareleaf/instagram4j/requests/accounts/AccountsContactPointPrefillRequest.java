package co.shareleaf.instagram4j.requests.accounts;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.IGConstants;
import co.shareleaf.instagram4j.models.IGBaseModel;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

public class AccountsContactPointPrefillRequest extends IGPostRequest<IGResponse> {

    @Override
    public String baseApiUrl() {
        return IGConstants.B_BASE_API_URL;
    }

    @Override
    protected IGBaseModel getPayload(IGClient client) {
        return new IGPayload();
    }

    @Override
    public String path() {
        return "accounts/contact_point_prefill/";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

    @Data
    private static class PrePayload extends IGBaseModel {
        private final String phone_id;
        private final String usage = "prefill";
    }

}
