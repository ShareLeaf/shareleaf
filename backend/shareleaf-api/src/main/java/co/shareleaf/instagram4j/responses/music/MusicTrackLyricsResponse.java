package co.shareleaf.instagram4j.responses.music;

import co.shareleaf.instagram4j.models.music.MusicLyrics;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.Data;

@Data
public class MusicTrackLyricsResponse extends IGResponse {
    private MusicLyrics lyrics;
}
