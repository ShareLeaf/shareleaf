package co.shareleaf.service.parser;

import co.shareleaf.data.postgres.repo.MetadataRepo;
import co.shareleaf.props.AWSProps;
import co.shareleaf.service.aws.S3Service;
import com.gargoylesoftware.htmlunit.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Bizuwork Melesse
 * created on 7/9/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramParser extends BaseParserService implements ParserService {
    private final MetadataRepo metadataRepo;
    private final S3Service s3Service;
    private final AWSProps awsProps;

    @Override
    public void processSoup(String soup, String url, String contentId, WebClient client) {
        downloadReel(url, contentId);
    }

    private void downloadReel(String url, String contentId) {

    }
}
