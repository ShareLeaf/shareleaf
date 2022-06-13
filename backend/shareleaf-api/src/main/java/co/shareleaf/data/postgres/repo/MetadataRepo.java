package co.shareleaf.data.postgres.repo;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Repository
@Transactional
public interface MetadataRepo extends ReactiveCrudRepository<MetadataEntity, String> {
}
