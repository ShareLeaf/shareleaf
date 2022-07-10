package co.shareleaf.instagram4j.login;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import co.shareleaf.ServiceTestConfiguration;
import co.shareleaf.props.InstagramProps;
import org.hamcrest.CoreMatchers;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.accounts.AccountsCurrentUserRequest;
import co.shareleaf.instagram4j.responses.IGResponse;
import lombok.extern.slf4j.Slf4j;
import co.shareleaf.instagram4j.serialize.SerializeTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertNotNull;

@Slf4j
@SpringBootTest(classes = ServiceTestConfiguration.class)
public class LoginTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private InstagramProps instagramProps;

    @Test
    public void loginTest() throws Exception {
        IGClient client = IGClient.builder()
                .username(instagramProps.getUsername())
                .password(instagramProps.getPassword())
                .client(SerializeTestUtil.formTestHttpClientBuilder().build())
//                .onLogin((cli, response) -> { // requires a challenge
//                    cli.sendRequest(new AccountsCurrentUserRequest())
//                    .thenAccept(_response -> {
//                        assertThat(_response.getStatus(), CoreMatchers.is("ok"));
//                    })
//                    .join();
//                })
                .login();
        log.debug(client.toString());
        assertNotNull(client.getSelfProfile());
        log.debug("Success");
    }

    public static void postLoginResponsesHandler(List<CompletableFuture<?>> responses) {
        responses.stream()
                .map(res -> res.thenApply(IGResponse.class::cast))
                .forEach(res -> {
                    res.thenAccept(igRes -> {
                        log.info("{} : {}", igRes.getClass().getName(), igRes.getStatus());
                    });
                });
    }
}
