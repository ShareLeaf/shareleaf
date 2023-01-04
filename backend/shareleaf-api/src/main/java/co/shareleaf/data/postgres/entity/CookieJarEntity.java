package co.shareleaf.data.postgres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Bizuwork Melesse
 * created on 7/10/22
 */
@Getter
@Setter
@Entity
@Table(schema = "public", name = "cookie_jar")
public class CookieJarEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_key")
    private String key;

    @Column(name = "c_path")
    private String path;

    @Column(name = "c_domain")
    private String domain;

    @Column(name = "c_name")
    private String name;

    @Column(name = "c_value")
    private String value;

    @Column(name = "c_username")
    private String username;

    @Column(name = "c_expires_at")
    private Long expiresAt;

    @Column(name = "c_host_only")
    private Boolean hostOnly;

    @Column(name = "c_http_only")
    private Boolean httpOnly;

    @Column(name = "c_persistent")
    private Boolean persistent;

    @Column(name = "c_secure")
    private Boolean secure;

    @Column(name = "created_dt")
    private LocalDateTime createdDt = LocalDateTime.now(ZoneId.of("UTC"));
    
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt = LocalDateTime.now(ZoneId.of("UTC"));
}