package co.shareleaf.data.postgres.repo;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Repository
@Transactional
public interface MetadataRepo extends ReactiveCrudRepository<MetadataEntity, Long> {

    @Query("select * from metadata where canonical_url = $1")
    Mono<MetadataEntity> findByCanonicalUrl(String url);
}
