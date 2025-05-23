package co.shareleaf.instagram4j.actions.users;

import java.util.concurrent.CompletableFuture;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.requests.users.UsersInfoRequest;
import co.shareleaf.instagram4j.requests.users.UsersSearchRequest;
import co.shareleaf.instagram4j.requests.users.UsersUsernameInfoRequest;
import co.shareleaf.instagram4j.responses.users.UserResponse;
import co.shareleaf.instagram4j.responses.users.UsersSearchResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsersAction {
    @NonNull
    private IGClient client;

    public CompletableFuture<UserAction> findByUsername(String username) {
        return new UsersUsernameInfoRequest(username).execute(client)
                .thenApply(response -> new UserAction(client, response.getUser()));
    }

    public CompletableFuture<User> info(long pk) {
        return new UsersInfoRequest(pk).execute(client)
                .thenApply(UserResponse::getUser);
    }

    public CompletableFuture<UsersSearchResponse> search(String query) {
        return new UsersSearchRequest(query).execute(client);
    }

}
