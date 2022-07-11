package co.shareleaf.instagram4j.actions.simulate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.actions.async.AsyncAction;
import co.shareleaf.instagram4j.requests.IGRequest;
import co.shareleaf.instagram4j.requests.accounts.AccountsContactPointPrefillRequest;
import co.shareleaf.instagram4j.requests.accounts.AccountsGetPrefillCandidatesRequest;
import co.shareleaf.instagram4j.requests.direct.DirectGetPresenceRequest;
import co.shareleaf.instagram4j.requests.direct.DirectInboxRequest;
import co.shareleaf.instagram4j.requests.discover.DiscoverTopicalExploreRequest;
import co.shareleaf.instagram4j.requests.feed.FeedReelsTrayRequest;
import co.shareleaf.instagram4j.requests.feed.FeedTimelineRequest;
import co.shareleaf.instagram4j.requests.launcher.LauncherSyncRequest;
import co.shareleaf.instagram4j.requests.linkedaccounts.LinkedAccountsGetLinkageStatusRequest;
import co.shareleaf.instagram4j.requests.loom.LoomFetchConfigRequest;
import co.shareleaf.instagram4j.requests.media.MediaBlockedRequest;
import co.shareleaf.instagram4j.requests.multipleaccounts.MultipleAccountsGetAccountFamilyRequest;
import co.shareleaf.instagram4j.requests.news.NewsInboxRequest;
import co.shareleaf.instagram4j.requests.qe.QeSyncRequest;
import co.shareleaf.instagram4j.requests.qp.QpGetCooldowns;
import co.shareleaf.instagram4j.requests.status.StatusGetViewableStatusesRequest;
import co.shareleaf.instagram4j.requests.users.UsersArlinkDownloadInfoRequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimulateAction {
    @NonNull
    private IGClient client;

    private static final IGRequest<?>[] preLoginFlow = {
            new LauncherSyncRequest(true),
            new QeSyncRequest(true),
            new AccountsContactPointPrefillRequest(),
            new AccountsGetPrefillCandidatesRequest()
    };

    private static final IGRequest<?>[] postLoginFlow = {
            new LauncherSyncRequest(),
            new QpGetCooldowns(),
            new MultipleAccountsGetAccountFamilyRequest(),
            new LinkedAccountsGetLinkageStatusRequest(),
            new LoomFetchConfigRequest(),
            new MediaBlockedRequest(),
            new FeedTimelineRequest(),
            new FeedReelsTrayRequest(),
            new UsersArlinkDownloadInfoRequest(),
            new DiscoverTopicalExploreRequest().is_prefetch(true),
            new NewsInboxRequest(false),
            new DirectGetPresenceRequest(),
            new DirectInboxRequest().limit(0).visual_message_return_type("unseen")
                    .persistent_badging(true),
            new DirectInboxRequest().limit(20).fetch_reason("initial_snapshot")
                    .thread_message_limit(10).visual_message_return_type("unseen")
                    .persistent_badging(true),
            new StatusGetViewableStatusesRequest()
    };

    public List<CompletableFuture<?>> preLoginFlow() {
        return AsyncAction.executeRequestsAsync(client, preLoginFlow);
    }

    public List<CompletableFuture<?>> postLoginFlow() {
        return AsyncAction.executeRequestsAsync(client, postLoginFlow);
    }
}
