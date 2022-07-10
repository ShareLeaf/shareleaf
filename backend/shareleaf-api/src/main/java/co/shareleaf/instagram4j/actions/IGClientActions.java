package co.shareleaf.instagram4j.actions;

import java.lang.reflect.Field;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.actions.account.AccountAction;
import co.shareleaf.instagram4j.actions.igtv.IgtvAction;
import co.shareleaf.instagram4j.actions.search.SearchAction;
import co.shareleaf.instagram4j.actions.simulate.SimulateAction;
import co.shareleaf.instagram4j.actions.status.StatusAction;
import co.shareleaf.instagram4j.actions.story.StoryAction;
import co.shareleaf.instagram4j.actions.timeline.TimelineAction;
import co.shareleaf.instagram4j.actions.upload.UploadAction;
import co.shareleaf.instagram4j.actions.users.UsersAction;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(fluent = true, prefix = "_")
@Getter
public class IGClientActions {
    private UploadAction _upload;
    private TimelineAction _timeline;
    private StoryAction _story;
    private UsersAction _users;
    private SimulateAction _simulate;
    private IgtvAction _igtv;
    private AccountAction _account;
    private SearchAction _search;
    private StatusAction _status;

    @SneakyThrows
    public IGClientActions(IGClient client) {
        for (Field field : this.getClass().getDeclaredFields())
            if (field.getName().startsWith("_"))
                field.set(this, field.getType().getConstructor(IGClient.class).newInstance(client));
    }

}
