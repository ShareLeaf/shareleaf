package co.shareleaf.instagram4j.actions.users;

import java.util.concurrent.CompletableFuture;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.actions.feed.FeedIterable;
import co.shareleaf.instagram4j.models.friendships.Friendship;
import co.shareleaf.instagram4j.models.user.User;
import co.shareleaf.instagram4j.requests.friendships.FriendshipsActionRequest;
import co.shareleaf.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction;
import co.shareleaf.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import co.shareleaf.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds;
import co.shareleaf.instagram4j.requests.friendships.FriendshipsShowRequest;
import co.shareleaf.instagram4j.responses.feed.FeedUsersResponse;
import co.shareleaf.instagram4j.responses.friendships.FriendshipStatusResponse;
import co.shareleaf.instagram4j.responses.friendships.FriendshipsShowResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserAction {
    @NonNull
    private IGClient client;
    @NonNull
    @Getter
    private User user;

    public FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> followersFeed() {
        return new FeedIterable<>(client, () ->
                new FriendshipsFeedsRequest(user.getPk(), FriendshipsFeeds.FOLLOWERS));
    }

    public FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> followingFeed() {
        return new FeedIterable<>(client, () ->
                new FriendshipsFeedsRequest(user.getPk(), FriendshipsFeeds.FOLLOWING));
    }

    public CompletableFuture<Friendship> getFriendship() {
        return new FriendshipsShowRequest(user.getPk()).execute(client)
                .thenApply(FriendshipsShowResponse::getFriendship);
    }

    public CompletableFuture<FriendshipStatusResponse> action(FriendshipsAction action) {
        return new FriendshipsActionRequest(user.getPk(), action).execute(client);
    }
}
