package co.shareleaf.service.content;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.model.*;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.WebsiteProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.parser.BaseParserService;
import co.shareleaf.service.scraper.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl extends BaseParserService implements ContentService {
    private final MetadataRepo metadataRepo;
    private final AWSProps awsProps;
    private final WebsiteProps websiteProps;
    private final ScraperService scraperService;
    private final S3Service s3Service;
    
    public Mono<SLContentMetadata> generateContentId(SLContentUrl url) {
        // TODO: delete after testing
//        processUrl(url.getUrl()).map(it -> true);
        // TODO: end delete
        if (isValidUrl(url)) {
            // If a record exist, return its content id. Otherwise,
            // create a new record and kick off the crawling
            // process
            return metadataRepo
                    .findByCanonicalUrl(url.getUrl())
                    .map(it -> new SLContentMetadata()
                            .shareableLink(getShareableLink(it, websiteProps.getBaseUrl()))
                            .contentId(it.getContentId()))
                    .switchIfEmpty(Mono.defer(() -> processUrl(url.getUrl())));
        }
        return Mono.just(new SLContentMetadata().error(true));
    }

    private Mono<SLContentMetadata> processUrl(String url) {
        log.info("Processing new url: {}", url);
        MetadataEntity record = new MetadataEntity();
        String contentId = generateUid();
        record.setContentId(contentId);
        record.setCanonicalUrl(url);
        Mono.fromCallable(() -> scraperService.getContent(contentId, url))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(it -> log.error(it.getLocalizedMessage()))
                .subscribe();
        return metadataRepo
                .save(record)
                .doOnError(it -> log.error(it.getLocalizedMessage()))
                .map(it -> new SLContentMetadata()
                        .shareableLink(getShareableLink(it, websiteProps.getBaseUrl()))
                        .contentId(contentId));
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
                                    .contentId(it.getContentId())
                                    .videoUrl(getContentUrl(it.getContentId(), VIDEO, awsProps.getCdn()))
                                    .imageUrl(getContentUrl(it.getContentId(), IMAGE, awsProps.getCdn()))
                                    .shareableLink(getShareableLink(it, websiteProps.getBaseUrl()))
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
                                    .error(false)
                                    .createdDt(it.getCreatedDt().toEpochSecond(ZoneOffset.UTC))
                    ).switchIfEmpty(Mono.defer(() -> Mono.just(new SLContentMetadata().error(true))));
        }
        return Mono.just(new SLContentMetadata().error(true));
    }


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

    private boolean isValidUrl(SLContentUrl url) {
        if (!ObjectUtils.isEmpty(url.getUrl())) {
            try {
                URI uri = new URI(url.getUrl());
                if (!ObjectUtils.isEmpty(uri.getHost())) {
                    return true;
                }
            } catch (URISyntaxException e) {
                return false;
            }
        }
        return false;
    }
}
