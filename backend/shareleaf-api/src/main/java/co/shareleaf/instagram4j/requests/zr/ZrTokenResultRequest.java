package co.shareleaf.instagram4j.requests.zr;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

public class ZrTokenResultRequest extends IGGetRequest<IGResponse> {

    @Override
    public String path() {
        return "zr/token/result/";
    }

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("device_id", client.getDeviceId(), "custom_device_id",
                client.getGuid(), "fetch_reason", "token_stale");
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

}
