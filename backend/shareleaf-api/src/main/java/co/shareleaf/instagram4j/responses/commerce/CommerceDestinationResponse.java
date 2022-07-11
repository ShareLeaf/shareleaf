package co.shareleaf.instagram4j.responses.commerce;

import java.util.List;
import co.shareleaf.instagram4j.models.discover.SectionalMediaGridItem;
import co.shareleaf.instagram4j.responses.IGPaginatedResponse;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.Data;

@Data
public class CommerceDestinationResponse extends IGResponse implements IGPaginatedResponse {
    private List<SectionalMediaGridItem> sectional_items;
    private String rank_token;
    private String next_max_id;
    private boolean more_available;
}
