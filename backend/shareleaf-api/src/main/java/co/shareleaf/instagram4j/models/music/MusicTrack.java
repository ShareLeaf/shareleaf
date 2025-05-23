package co.shareleaf.instagram4j.models.music;

import com.fasterxml.jackson.annotation.JsonProperty;
import co.shareleaf.instagram4j.models.IGBaseModel;
import lombok.Data;

@Data
public class MusicTrack extends IGBaseModel {
    private String audio_cluster_id;
    private String audio_asset_id;
    private long duration_in_ms;
    private String id;
    private String title;
    private String subtitle;
    private String display_artist;
    private String cover_artwork_uri;
    private String cover_artwork_thumbnail_uri;
    private String progressive_download_url;
    private long[] highlight_start_times_in_ms;
    @JsonProperty("is_explicit")
    private boolean is_explicit;
    private boolean has_lyrics;
}
