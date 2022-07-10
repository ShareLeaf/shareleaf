package co.shareleaf.instagram4j.responses.discover;

import java.util.List;
import co.shareleaf.instagram4j.models.discover.Cluster;
import co.shareleaf.instagram4j.models.discover.SectionalItem;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class DiscoverTopicalExploreResponse extends IGResponse implements IGPaginatedResponse {
    private List<SectionalItem> sectional_items;
    private String rank_token;
    private List<Cluster> clusters;
    private String next_max_id;
    private boolean more_available;
}
