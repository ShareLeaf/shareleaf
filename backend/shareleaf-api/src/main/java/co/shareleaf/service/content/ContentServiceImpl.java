package co.shareleaf.service.content;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.model.*;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.WebsiteProps;
import co.shareleaf.service.scraper.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
    private final MetadataRepo metadataRepo;
    private final AWSProps awsProps;
    private final WebsiteProps websiteProps;
    private final ScraperService scraperService;

    @Override
    public Mono<SLContentId> generateContentId(SLContentUrl url) {
        if (!ObjectUtils.isEmpty(url.getUrl())) {
            // If a record exist, return its content id. Otherwise,
            // create a new record and kick off the crawling
            // process
            return metadataRepo
                    .findByCanonicalUrl(url.getUrl())
                    .map(it -> new SLContentId().uid(it.getContentId()))
                    .switchIfEmpty(Mono.defer(() -> processUrl(url.getUrl())));
        }
        return Mono.just(new SLContentId());
    }

    private Mono<SLContentId> processUrl(String url) {
        MetadataEntity record = new MetadataEntity();
        String uid = generateUid();
        record.setContentId(uid);
        record.setCanonicalUrl(url);
        Mono.fromCallable(() -> scraperService.getContent(uid, url))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(Throwable::printStackTrace)
                .subscribe();
        return metadataRepo
                .save(record)
                .map(it -> new SLContentId().uid(uid));
    }

    @Override
    public Mono<SLContentMetadata> getMetadata(String uid) {
        if (!ObjectUtils.isEmpty(uid)) {
            // Update view count when fetching the metadata for this uid in parallel
            Mono<MetadataEntity> metadataEntityMono = metadataRepo.findByContentId(uid);
            metadataEntityMono
                    .flatMap(it -> {
                        it.setViewCount(it.getViewCount()+1);
                        return metadataRepo.save(it);
                    }).subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
            return metadataEntityMono
                    .map(it ->
                            new SLContentMetadata()
                                    .uid(it.getContentId())
                                    .url(getContentUrl(uid, it.getMediaType()))
                                    .thumbnail(String.format("%s/%s_i", awsProps.getCdn(), it.getContentId()))
                                    .shareableLink(String.format("%s/%s", websiteProps.getBaseUrl(), it.getContentId()))
                                    .mediaType(it.getMediaType() != null ? SLMediaType.fromValue(it.getMediaType()) : null)
                                    .invalidUrl(it.getInvalidUrl())
                                    .processed(it.getProcessed())
                                    .encoding(it.getEncoding())
                                    .title(it.getTitle())
                                    .description(it.getDescription())
                                    .category(it.getCategory())
                                    .shareCount(it.getShareCount())
                                    .viewCount(it.getViewCount())
                                    .likeCount(it.getLikeCount())
                                    .dislikeCount(it.getDislikeCount())
                                    .createdDt(it.getCreatedDt().toEpochSecond(ZoneOffset.UTC))
                    ).switchIfEmpty(Mono.defer(() -> Mono.just(new SLContentMetadata())));
        }
        return Mono.just(new SLContentMetadata());
    }

    private String getContentUrl(String uid, String mediaType) {
        if (mediaType != null && mediaType.equals("image")) {
            return String.format("%s/%s_i", awsProps.getCdn(), uid);
        } else if (mediaType != null && mediaType.equals("gif")) {
            return String.format("%s/%s_g", awsProps.getCdn(), uid);
        }
        return String.format("%s/%s_v", awsProps.getCdn(), uid);
    }

    @Override
    public Mono<SLGenericResponse> incrementShareCount(SLContentId contentId) {
        if (!ObjectUtils.isEmpty(contentId) && !ObjectUtils.isEmpty(contentId.getUid())) {
            return metadataRepo
                    .findByContentId(contentId.getUid())
                    .flatMap(it -> {
                        it.setShareCount(it.getShareCount()+1);
                        return metadataRepo.save(it)
                                .map(res -> new SLGenericResponse().status("OK"));
                    })
                    .switchIfEmpty(Mono.defer(() ->
                            Mono.just(new SLGenericResponse()
                                    .status("Failed to update share count"))));
        }
        return Mono.just(new SLGenericResponse()
                .error("Content ID not provided"));
    }

    public String generateUid() {
        StringBuilder builder = new StringBuilder();
        // An ID length of N gives 62^N unique IDs
        int contentIdLength = 7;
        for (int i = 0; i < contentIdLength; i++) {
            builder.append(getRandomCharacter());
        }
       return builder.toString();
    }

    public Character getRandomCharacter() {
        Random random = new Random();
        String uidAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqprstuvwxyz0123456789";
        int index = random.nextInt(uidAlphabet.length());
        return uidAlphabet.charAt(index);
    }
}
