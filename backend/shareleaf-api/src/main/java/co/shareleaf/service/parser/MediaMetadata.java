package co.shareleaf.service.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Bizuwork Melesse
 * created on 6/21/22
 */
@Data
@AllArgsConstructor
public class MediaMetadata {
    private String imageUrl;
    private String gifUrl;
    private String videoUrl;
    private String audioUrl;
    private String mediaType;
    private String encoding;
    private String description;
}
