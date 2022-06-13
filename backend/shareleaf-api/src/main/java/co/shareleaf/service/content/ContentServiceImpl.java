package co.shareleaf.service.content;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.model.SLContentId;
import co.shareleaf.model.SLContentMetadata;
import co.shareleaf.model.SLContentUrl;
import co.shareleaf.model.SLGenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    @Override
    public Mono<SLContentId> generateContentId(SLContentUrl url) {
        return Mono.just(new SLContentId().uid(UUID.randomUUID().toString()));
    }

    @Override
    public Mono<SLContentMetadata> getMetadata(String uid) {
        return null;
    }

    @Override
    public Mono<SLGenericResponse> incrementShareCount(SLContentId contentId) {
        return null;
    }
}
