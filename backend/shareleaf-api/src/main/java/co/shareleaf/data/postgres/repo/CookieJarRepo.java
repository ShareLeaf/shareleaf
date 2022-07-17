package co.shareleaf.data.postgres.repo;

import co.shareleaf.data.postgres.entity.CookieJarEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Repository
@Transactional
public interface CookieJarRepo extends ReactiveCrudRepository<CookieJarEntity, Long> {

    @Query("select * from cookie_jar where c_key = :url and c_username = :username")
    Flux<CookieJarEntity> findCookies(String url, String username);

    @Query("select * from cookie_jar where c_key = :url")
    Flux<CookieJarEntity> findCookies(String url);

    @Query("select * from cookie_jar order by updated_dt desc")
    Flux<CookieJarEntity> findAll();

    @Query("select * from cookie_jar where c_name = 'sessionid' order by updated_dt desc")
    Flux<CookieJarEntity> findSessionId();

    @Query("select * from cookie_jar where c_username = :username and c_key in (:urls)")
    Flux<CookieJarEntity> findCookiesByUrls(String username, List<String> urls);
}
