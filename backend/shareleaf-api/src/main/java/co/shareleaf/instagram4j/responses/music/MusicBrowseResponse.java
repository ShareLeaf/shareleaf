package co.shareleaf.instagram4j.responses.music;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import co.shareleaf.instagram4j.models.music.MusicPlaylist;
import co.shareleaf.instagram4j.responses.IGResponse;
import co.shareleaf.instagram4j.utils.IGUtils;

import lombok.Data;

@Data
public class MusicBrowseResponse extends IGResponse {
    @JsonDeserialize(converter = BeanToIGMusicPlaylistConverter.class)
    private List<MusicPlaylist> items;

    private static class BeanToIGMusicPlaylistConverter
            extends StdConverter<List<Map<String, Object>>, List<MusicPlaylist>> {
        @Override
        public List<MusicPlaylist> convert(List<Map<String, Object>> value) {
            return value.stream()
                    .filter(m -> m.containsKey("playlist"))
                    .map(m -> IGUtils.convertToView(m.get("playlist"), MusicPlaylist.class))
                    .collect(Collectors.toList());
        }
    }
}
