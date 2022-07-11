package co.shareleaf.instagram4j.requests.igtv;

import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

public class IgtvCreationToolsRequest extends IGGetRequest<IGResponse> {

    @Override
    public String path() {
        return "igtv/igtv_creation_tools/";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }
}
