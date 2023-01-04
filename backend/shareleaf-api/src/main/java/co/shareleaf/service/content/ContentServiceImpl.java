package co.shareleaf.service.content;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.model.*;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.WebsiteProps;
import co.shareleaf.service.aws.S3Service;
import co.shareleaf.service.parser.BaseParserService;
import co.shareleaf.service.scraper.ScraperService;
import co.shareleaf.service.scraper.ScraperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
public class ContentServiceImpl extends BaseParserService implements ContentService {
    private final WebsiteProps websiteProps;
    private final ScraperService scraperService;

    public ContentServiceImpl(ObjectMapper objectMapper,
                              MetadataRepo metadataRepo,
                              S3Service s3Service,
                              AWSProps awsProps,
                              ScraperUtils scraperUtils,
                              WebsiteProps websiteProps,
                              ScraperService scraperService) {
        super(objectMapper, metadataRepo, s3Service, awsProps, scraperUtils);
        this.websiteProps = websiteProps;
        this.scraperService = scraperService;
    }

    @Override
    public SLContentMetadata generateContentId(SLContentUrl url) {
        if (isValidUrl(url)) {
            // If a record exist, return its content id. Otherwise,
            // create a new record and kick off the crawling
            // process
            MetadataEntity metadataEntity =  metadataRepo.findByCanonicalUrl(url.getUrl());
            if (metadataEntity == null) {
                return processUrl(url.getUrl());
            } else {
                return new SLContentMetadata()
                    .shareableLink(getShareableLink(metadataEntity, websiteProps.getBaseUrl()))
                    .contentId(metadataEntity.getContentId());
            }
        }
        return new SLContentMetadata().error(true);
    }

    private SLContentMetadata processUrl(String url) {
        MetadataEntity record = new MetadataEntity();
        String contentId = generateUid();
        record.setContentId(contentId);
        record.setCanonicalUrl(url);
        metadataRepo.save(record);
        scraperService.getContent(contentId, url);
        return new SLContentMetadata()
            .shareableLink(getShareableLink(record, websiteProps.getBaseUrl()))
            .contentId(contentId);
    }

    @Override
    public SLContentMetadata getMetadata(String uid) {
        if (!ObjectUtils.isEmpty(uid)) {
            // Update view count when fetching the metadata for this uid in parallel
            MetadataEntity metadataEntity = metadataRepo.findByContentId(uid);
            if (metadataEntity != null) {
                metadataEntity.setViewCount(metadataEntity.getViewCount() + 1);
                metadataRepo.save(metadataEntity);
                return new SLContentMetadata()
                    .contentId(metadataEntity.getContentId())
                    .videoUrl(getContentUrl(metadataEntity.getContentId(), VIDEO, awsProps.getCdn()))
                    .imageUrl(getContentUrl(metadataEntity.getContentId(), IMAGE, awsProps.getCdn()))
                    .shareableLink(getShareableLink(metadataEntity, websiteProps.getBaseUrl()))
                    .mediaType(
                        metadataEntity.getMediaType() != null ? SLMediaType.fromValue(metadataEntity.getMediaType())
                            : null)
                    .invalidUrl(metadataEntity.getInvalidUrl())
                    .processed(metadataEntity.getProcessed())
                    .encoding(metadataEntity.getEncoding())
                    .title(metadataEntity.getTitle())
                    .description(metadataEntity.getDescription())
                    .category(metadataEntity.getCategory())
                    .shareCount(metadataEntity.getShareCount())
                    .viewCount(metadataEntity.getViewCount())
                    .likeCount(metadataEntity.getLikeCount())
                    .dislikeCount(metadataEntity.getDislikeCount())
                    .error(false)
                    .createdDt(metadataEntity.getCreatedDt().toEpochSecond(ZoneOffset.UTC));
            }
        }
        return new SLContentMetadata().error(true);
    }


    @Override
    public SLGenericResponse incrementShareCount(SLContentId contentId) {
        if (!ObjectUtils.isEmpty(contentId) && !ObjectUtils.isEmpty(contentId.getUid())) {
            MetadataEntity metadataEntity = metadataRepo.findByContentId(contentId.getUid());
            if (metadataEntity != null) {
                metadataEntity.setShareCount(metadataEntity.getShareCount() + 1);
                metadataRepo.save(metadataEntity);
                return new SLGenericResponse().status("OK");
            } else {
                new SLGenericResponse()
                    .status("Failed to update share count");
            }
        }
        return new SLGenericResponse()
            .error("Content ID not provided");
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
