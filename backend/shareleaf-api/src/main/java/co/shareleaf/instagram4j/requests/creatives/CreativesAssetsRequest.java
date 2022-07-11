package co.shareleaf.instagram4j.requests.creatives;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.creatives.CreativesAssetsResponse;

import lombok.Data;

public class CreativesAssetsRequest extends IGPostRequest<CreativesAssetsResponse> {

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new CreativesAssetsPayload();
    }

    @Override
    public String path() {
        return "creatives/assets/";
    }

    @Override
    public Class<CreativesAssetsResponse> getResponseType() {
        return CreativesAssetsResponse.class;
    }

    @Data
    public static class CreativesAssetsPayload extends IGPayload {
        private final String type = "static_stickers";
    }
}
