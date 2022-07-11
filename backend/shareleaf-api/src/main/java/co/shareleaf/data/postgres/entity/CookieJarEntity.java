package co.shareleaf.data.postgres.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Bizuwork Melesse
 * created on 7/10/22
 */
@Getter
@Setter
@Table("public.cookie_jar")
public class CookieJarEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("c_key")
    private String key;

    @Column("c_path")
    private String path;

    @Column("c_domain")
    private String domain;

    @Column("c_name")
    private String name;

    @Column("c_value")
    private String value;

    @Column("c_username")
    private String username;

    @Column("c_expires_at")
    private Long expiresAt;

    @Column("c_host_only")
    private Boolean hostOnly;

    @Column("c_http_only")
    private Boolean httpOnly;

    @Column("c_persistent")
    private Boolean persistent;

    @Column("c_secure")
    private Boolean secure;

    @Column("created_dt")
    private LocalDateTime createdDt = LocalDateTime.now(ZoneId.of("UTC"));
    
    @Column("updated_dt")
    private LocalDateTime updatedDt = LocalDateTime.now(ZoneId.of("UTC"));
}