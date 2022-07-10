package co.shareleaf.instagram4j.login;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;

import co.shareleaf.ServiceTestConfiguration;
import co.shareleaf.props.InstagramProps;
import lombok.extern.slf4j.Slf4j;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.IGClient.Builder.LoginHandler;
import co.shareleaf.instagram4j.utils.IGChallengeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;


@Ignore
@Slf4j
@SpringBootTest(classes = ServiceTestConfiguration.class)
public class TwoFactorLoginTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private InstagramProps instagramProps;

    @Test
    public void testTwoFactor() throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Callable that returns inputted code from System.in
        Callable<String> inputCode = () -> {
            System.out.print("Please input code: ");
            return scanner.nextLine();
        };

        // handler for two factor login
        LoginHandler twoFactorHandler = (client, response) -> {
            // included utility to resolve two factor
            return IGChallengeUtils.resolveTwoFactor(client, response, inputCode);
        };

        IGClient client = IGClient.builder()
                .username(instagramProps.getUsername())
                .password(instagramProps.getPassword())
                .onTwoFactor(twoFactorHandler)
                .simulatedLogin();

        assertNotNull(client.getSelfProfile());
    }
}
