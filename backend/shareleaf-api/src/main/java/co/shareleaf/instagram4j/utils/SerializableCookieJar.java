package co.shareleaf.instagram4j.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import co.shareleaf.data.postgres.entity.CookieJarEntity;
import co.shareleaf.data.postgres.repo.CookieJarRepo;
import co.shareleaf.props.InstagramProps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class SerializableCookieJar implements CookieJar, Serializable {
    private CookieJarRepo cookieJarRepo;
    private InstagramProps instagramProps;

    @Autowired
    public void setCookieJarRepo(CookieJarRepo cookieJarRepo) {
        this.cookieJarRepo = cookieJarRepo;
    }

    @Autowired
    public void setInstagramProps(InstagramProps instagramProps) {
        this.instagramProps = instagramProps;
    }

    private static final long serialVersionUID = -837498359387593793l;

    @NotNull
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<CookieJarEntity> cookieJarEntities = cookieJarRepo
                .findCookies(url.host(), instagramProps.getUsername());
        if (cookieJarEntities == null) return new ArrayList<>();
        return cookieJarEntities.stream().map(it -> {
            Cookie.Builder builder = new Cookie.Builder();
            builder.name(it.getName());
            builder.value(it.getValue());
            builder.path(it.getPath());
            builder.domain(it.getDomain());
            builder.expiresAt(it.getExpiresAt());
            if (it.getHttpOnly() != null && it.getHttpOnly()) {
                builder.httpOnly();
            }
            if (it.getHostOnly() != null && it.getHostOnly()) {
                builder.hostOnlyDomain(it.getDomain());
            }
            if (it.getExpiresAt() != null) {
                builder.expiresAt(it.getExpiresAt());
            }
            if (it.getSecure() != null && it.getSecure()) {
                builder.secure();
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public void saveFromResponse(HttpUrl url, @NotNull List<Cookie> cookies) {
        // Fetch the current cookies for the host and upsert values
        List<CookieJarEntity> cookieJarEntities = cookieJarRepo.findCookies(url.host(), instagramProps.getUsername());
        Map<String, CookieJarEntity> cookieEntities = new HashMap<>();
        for (CookieJarEntity cookieJarEntity : cookieJarEntities) {
            cookieEntities.put(cookieJarEntity.getName(), cookieJarEntity);
            for (Cookie c : cookies) {
                log.info("Cookie - {}: {} exp: {}", c.name(), c.value(), c.expiresAt());
                if (c.name().equalsIgnoreCase("sessionid") && ObjectUtils.isEmpty(c.value())) {
                    // do not update the session ID if it has been stripped from the response
                    // header
                    continue;
                }
                if (cookieEntities.containsKey(c.name())) {
                    CookieJarEntity existingCookie = cookieEntities.get(c.name());
                    mapCookieToEntity(url, existingCookie, c);
                    existingCookie.setUpdatedDt(LocalDateTime.now(ZoneId.of("UTC")));
                } else {
                    CookieJarEntity jarEntity = new CookieJarEntity();
                    mapCookieToEntity(url, jarEntity, c);
                    cookieEntities.put(c.name(), jarEntity);
                }
            }
        }
        if (!cookieEntities.isEmpty()) {
            log.info("Updating cookies for {}", url.host());
            cookieJarRepo.saveAll(cookieEntities.values());
        }
    }

    private void mapCookieToEntity(HttpUrl url, CookieJarEntity jarEntity, Cookie c) {
        jarEntity.setId(jarEntity.getId());
        jarEntity.setKey(url.host());
        jarEntity.setPath(c.path());
        jarEntity.setName(c.name());
        jarEntity.setDomain(c.domain());
        jarEntity.setHttpOnly(c.httpOnly());
        jarEntity.setHostOnly(c.hostOnly());
        jarEntity.setPersistent(c.persistent());
        jarEntity.setSecure(c.secure());
        jarEntity.setValue(c.value());
        jarEntity.setExpiresAt(c.expiresAt());
        jarEntity.setUsername(instagramProps.getUsername());
    }

    @AllArgsConstructor
    public static class SerializableCookie implements Serializable {

        private static final long serialVersionUID = -8594045714036645534L;

        private transient Cookie cookie;

        private static long NON_VALID_EXPIRES_AT = -1L;

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(cookie.name());
            out.writeObject(cookie.value());
            out.writeLong(cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
            out.writeObject(cookie.domain());
            out.writeObject(cookie.path());
            out.writeBoolean(cookie.secure());
            out.writeBoolean(cookie.httpOnly());
            out.writeBoolean(cookie.hostOnly());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            Cookie.Builder builder = new Cookie.Builder();

            builder.name((String) in.readObject());

            builder.value((String) in.readObject());

            long expiresAt = in.readLong();
            if (expiresAt != NON_VALID_EXPIRES_AT) {
                builder.expiresAt(expiresAt);
            }

            final String domain = (String) in.readObject();
            builder.domain(domain);

            builder.path((String) in.readObject());

            if (in.readBoolean())
                builder.secure();

            if (in.readBoolean())
                builder.httpOnly();

            if (in.readBoolean())
                builder.hostOnlyDomain(domain);

            cookie = builder.build();
        }

    }

}
