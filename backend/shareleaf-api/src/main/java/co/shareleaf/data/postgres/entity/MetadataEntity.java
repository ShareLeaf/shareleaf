package co.shareleaf.data.postgres.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * @author Bizuwork Melesse
 * created on 3/14/22
 */
@Getter
@Setter
@Table("public.metadata")
public class MetadataEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("content_id")
    private String contentId;

    @Column("invalid_url")
    private Boolean invalidUrl = false;

    @Column("processed")
    private Boolean processed = false;

    @Column("media_type")
    private String mediaType;

    @Column("encoding")
    private String encoding;

    @Column("canonical_url")
    private String canonicalUrl;

    @Column("title")
    private String title;

    @Column("category")
    private String category;

    @Column("description")
    private String description;

    @Column("like_count")
    private Long likeCount = 0L;

    @Column("share_count")
    private Long shareCount = 0L;

    @Column("view_count")
    private Long viewCount = 0L;

    @Column("dislike_count")
    private Long dislikeCount = 0L;

    @Column("created_dt")
    private LocalDateTime createdDt = LocalDateTime.now(ZoneId.of("UTC"));
    
    @Column("updated_dt")
    private LocalDateTime updatedDt = LocalDateTime.now(ZoneId.of("UTC"));
}