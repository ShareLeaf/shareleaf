package co.shareleaf.data.postgres.repo;

import co.shareleaf.data.postgres.entity.CookieJarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Repository
@Transactional
public interface CookieJarRepo extends JpaRepository<CookieJarEntity, Long> {

    @Query(value = "select * from cookie_jar where c_key = ?1 and c_username = ?2", nativeQuery = true)
    List<CookieJarEntity> findCookies(String url, String username);

    @Query(value = "select * from cookie_jar where c_key = ?1", nativeQuery = true)
    List<CookieJarEntity> findCookies(String url);

    @Query(value = "select * from cookie_jar order by updated_dt desc", nativeQuery = true)
    List<CookieJarEntity> findAll();

    @Query(value = "select * from cookie_jar where c_name = 'sessionid' order by updated_dt desc", nativeQuery = true)
    List<CookieJarEntity> findSessionId();

    @Query(value = "select * from cookie_jar where c_username = ?1 and c_key in (?2)", nativeQuery = true)
    List<CookieJarEntity> findCookiesByUrls(String username, List<String> urls);
}
