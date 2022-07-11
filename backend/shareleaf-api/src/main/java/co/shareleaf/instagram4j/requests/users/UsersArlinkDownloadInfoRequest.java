package co.shareleaf.instagram4j.requests.users;

import co.shareleaf.instagram4j.requests.IGGetRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

public class UsersArlinkDownloadInfoRequest extends IGGetRequest<IGResponse> {

    @Override
    public String path() {
        return "users/arlink_download_info/?version_override=2.2.1";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

}
