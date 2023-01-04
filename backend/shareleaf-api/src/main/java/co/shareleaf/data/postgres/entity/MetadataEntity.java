package co.shareleaf.data.postgres.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Bizuwork Melesse
 * created on 3/14/22
 */
@Getter
@Setter
@Table(schema = "public", name = "metadata")
public class MetadataEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "invalid_url")
    private Boolean invalidUrl = false;

    @Column(name = "processed")
    private Boolean processed = false;

    @Column(name = "has_audio")
    private Boolean hasAudio = false;

    @Column(name = "title")
    private String title = "";

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "encoding")
    private String encoding;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "share_count")
    private Long shareCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "dislike_count")
    private Long dislikeCount = 0L;

    @Column(name = "created_dt")
    private LocalDateTime createdDt = LocalDateTime.now(ZoneId.of("UTC"));
    
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt = LocalDateTime.now(ZoneId.of("UTC"));
}