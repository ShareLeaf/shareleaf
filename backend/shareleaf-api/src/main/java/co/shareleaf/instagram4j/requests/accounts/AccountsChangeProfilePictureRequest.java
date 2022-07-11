package co.shareleaf.instagram4j.requests.accounts;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.IGPayload;
import co.shareleaf.instagram4j.requests.IGPostRequest;
import co.shareleaf.instagram4j.responses.accounts.AccountsUserResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountsChangeProfilePictureRequest extends IGPostRequest<AccountsUserResponse> {
    @NonNull
    private String _upload_id;

    @Override
    protected IGPayload getPayload(IGClient client) {
        return new IGPayload() {
            @Getter
            private String upload_id = _upload_id;
        };
    }

    @Override
    public String path() {
        return "accounts/change_profile_picture/";
    }

    @Override
    public Class<AccountsUserResponse> getResponseType() {
        return AccountsUserResponse.class;
    }

}
