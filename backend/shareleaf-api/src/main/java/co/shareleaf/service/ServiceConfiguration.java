package co.shareleaf.service;


import co.shareleaf.data.postgres.repo.CookieJarRepo;
import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.utils.IGUtils;
import co.shareleaf.instagram4j.utils.SerializableCookieJar;
import co.shareleaf.props.AWSProps;
import co.shareleaf.props.InstagramProps;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 *
 */
@Slf4j
@Configuration
@ComponentScan
@RequiredArgsConstructor
public class ServiceConfiguration {
    private final AWSProps awsProps;
    private final InstagramProps instagramProps;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(
                awsProps.getAccessKey(),
                awsProps.getSecretKey()
        );
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsProps.getRegion())
                .build();
    }

    @Bean
    public IGClient igClient(SerializableCookieJar cookieJar) {
        IGUtils.serializableCookieJar = cookieJar;
        return IGClient.builder()
            .username(instagramProps.getUsername())
            .password(instagramProps.getPassword())
            .build();
    }
}