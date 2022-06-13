package co.shareleaf.data.postgres.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author Bizuwork Melesse
 * created on 3/14/22
 */
@Getter
@Setter
//@Table(schema = "public", name = "metadata")
public class MetadataEntity {

    @Id
    @Column("id")
    private String id;

    @Column("invalid_url")
    private Boolean invalidUrl;

    @Column("processed")
    private Boolean processed;

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
    private Long likeCount;

    @Column("share_count")
    private Long shareCount;

    @Column("view_count")
    private Long viewCount;

    @Column("dislike_count")
    private Long dislikeCount;

    @Column("created_dt")
    private LocalDateTime createdDt;
    
    @Column("updated_dt")
    private LocalDateTime updatedDt;
}