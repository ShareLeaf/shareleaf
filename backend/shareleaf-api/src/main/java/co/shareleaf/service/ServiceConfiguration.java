package co.shareleaf.service;


import co.shareleaf.data.postgres.entity.CookieJarEntity;
import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.CookieJarRepo;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.exceptions.IGLoginException;
import co.shareleaf.instagram4j.utils.IGUtils;
import co.shareleaf.instagram4j.utils.SerializableCookieJar;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.InstagramProps;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 *
 */
@Slf4j
@Configuration
@ComponentScan
@RequiredArgsConstructor
public class ServiceConfiguration {
    private final AWSProps awsProps;
    private final InstagramProps instagramProps;
    private final CookieJarRepo cookieJarRepo;
    private final ObjectMapper mapper;
    private final  List<String> urls = List.of(
            "b.i.instagram.com",
            "i.instagram.com",
            "www.instagram.com",
            "instagram.com"
    );

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(
                awsProps.getAccessKey(),
                awsProps.getSecretKey()
        );
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsProps.getRegion())
                .build();
    }

    @Bean
    public IGClient igClient(@Qualifier("db-migration") Boolean dbMigrated, SerializableCookieJar cookieJar) throws IOException {
        IGUtils.serializableCookieJar = cookieJar;
//        return IGClient.builder().build();
////        if (dbMigrated) {
////            if (doLogin()) {
            return IGClient.builder()
                    .username(instagramProps.getUsername())
                    .password(instagramProps.getPassword()).build();
//                    .login();
////            }
//            log.info("Instagram cookies have been loaded");
//            return IGClient.builder().build();
//        }
//        throw new BeanCreationException("Failed to create igClient bean because DB migration has not happened yet");
    }

    private boolean doLogin() throws IOException {
        // Check the database if there non-expired cookies. If not, attempt to load them from
        // the default json file. Determine if login is required after loading the cookies.
        List<CookieJarEntity> cookieJarEntities = cookieJarRepo
                .findCookiesByUrls(instagramProps.getUsername(), urls)
                .collectList()
                .block();

        // If the session ID is set to an empty string or
//        boolean emptySessionId = false;
//
//        if (!ObjectUtils.isEmpty(cookieJarEntities)) {
//            for (CookieJarEntity c : cookieJarEntities) {
//                String value = c.getValue().replace("\"", "").strip();
//                if (c.getName().equalsIgnoreCase("sessionid") &&
//                        ObjectUtils.isEmpty(value)) {
//                    emptySessionId = true;
//                }
//            }
//        }
//        if (ObjectUtils.isEmpty(cookieJarEntities) || emptySessionId) {
//            return loadFromFile();
//        }
        if (cookieJarEntities == null) return true;
        return expiredCsrfToken(cookieJarEntities);
    }

    private boolean loadFromFile() throws IOException {
        List<CookieJarEntity> allCookies = new ArrayList<>();
        InputStream resource = new ClassPathResource("cookies/instagram.json").getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource)) ) {
            String json = FileCopyUtils.copyToString(reader);
            TypeReference<Map<String, List<JsonNode>>> typeRef = new TypeReference<>() {
            };
            Map<String, List<JsonNode>> cookieFile = mapper.readValue(json, typeRef);
            for (Map.Entry<String, List<JsonNode>> entry : cookieFile.entrySet()) {
                String host = entry.getKey();
                Map<String, CookieJarEntity> uniqueCookies = new HashMap<>();
                for (JsonNode cookie : entry.getValue()) {
                    CookieJarEntity entity = new CookieJarEntity();
                    entity.setKey(host);
                    entity.setName(cookie.get("name").asText());
                    entity.setPath(cookie.get("path").asText());
                    entity.setDomain(cookie.get("domain").asText());
                    entity.setValue(cookie.get("value").asText());
                    entity.setUsername(instagramProps.getUsername());
                    entity.setHostOnly(cookie.get("hostOnly").asBoolean());
                    entity.setHttpOnly(cookie.get("httpOnly").asBoolean());
                    entity.setPersistent(cookie.get("persistent").asBoolean());
                    entity.setSecure(cookie.get("secure").asBoolean());
                    entity.setExpiresAt(cookie.get("expiresAt").asLong());
                    uniqueCookies.put(entity.getName(), entity);
                }
                allCookies.addAll(uniqueCookies.values());
            }

            // Fetch any existing cookies from the database and update duplicates
            List<CookieJarEntity> cookieJarEntities = cookieJarRepo
                    .findCookiesByUrls(instagramProps.getUsername(), urls)
                    .collectList()
                    .block();
            Map<String, CookieJarEntity> existingEntityMap = new HashMap<>();
            if (!ObjectUtils.isEmpty(cookieJarEntities)) {
                for (CookieJarEntity cookieJarEntity : cookieJarEntities) {
                    existingEntityMap.put(cookieJarEntity.getName(), cookieJarEntity);
                }
            }
            List<CookieJarEntity> newCookies = new ArrayList<>();
            for (CookieJarEntity newCookie : allCookies) {
                if (!existingEntityMap.containsKey(newCookie.getName())) {
                    newCookies.add(newCookie);
                }
            }
            // Only add cookies that are not already in the database
            cookieJarRepo.saveAll(newCookies)
                    .collectList()
                    .block();
            allCookies.addAll(newCookies);
        }
        return expiredCsrfToken(allCookies);
    }

    private boolean expiredCsrfToken(List<CookieJarEntity> cookieJarEntities) {
        for (CookieJarEntity cookie : cookieJarEntities) {
            if (cookie.getName().equals("ds_user_id") || cookie.getName().equals("csrftoken")) {
                // Verify that the csrf token has at least 1 day of life left
                if (cookie.getExpiresAt() != null && cookie.getExpiresAt() > LocalDateTime
                        .now()
                        .plusDays(1)
                        .toEpochSecond(ZoneOffset.UTC)) {
                    return false;
                }
                return true;
            }
        }
        return true;
    }
}