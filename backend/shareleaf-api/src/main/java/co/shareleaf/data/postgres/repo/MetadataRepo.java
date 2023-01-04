package co.shareleaf.data.postgres.repo;

import co.shareleaf.data.postgres.entity.MetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Repository
@Transactional
public interface MetadataRepo extends JpaRepository<MetadataEntity, Long> {

    @Query(value = "select * from metadata where canonical_url = ?1", nativeQuery = true)
    MetadataEntity findByCanonicalUrl(String url);

    @Query(value = "select * from metadata where content_id = ?1", nativeQuery = true)
    MetadataEntity findByContentId(String uid);
}
