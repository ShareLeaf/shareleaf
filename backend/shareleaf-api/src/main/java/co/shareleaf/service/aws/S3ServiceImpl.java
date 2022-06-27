package co.shareleaf.service.aws;


import co.shareleaf.props.AWSProps;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bizuwork Melesse
 * created on 2/13/22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    private final AWSProps awsProps;

    @Override
    public void uploadImage(String bucket, String key, InputStream stream, String contentType) {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(stream.available());
            meta.setContentType(contentType);
            s3Client.putObject(new PutObjectRequest(
                    bucket, key, stream, meta)
                    .withCannedAcl(CannedAccessControlList.Private));
            log.info("S3ServiceImpl.uploadContent Uploaded content with filename {}/{} to S3", awsProps.getCdn(), key);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadHlsData(String bucket, String contentId) {
        File f = new File(".");
        String[] pathnames = f.list();
        List<String> hlsFiles = new ArrayList<>();
        List<String> toDelete = new ArrayList<>();
        if (pathnames != null) {
            for (String p : pathnames) {
                if (p.contains(contentId) && (p.contains(".ts") || p.contains(".m3u8"))) {
                    hlsFiles.add(p);
                }
                if (p.contains(contentId)) {
                    // Add all files (HLS and MP4) for deletion
                    toDelete.add(p);
                }
            }
        }
        String folder = contentId + "/";
        log.info("Uploading HLS files to S3 for content ID {}", contentId);
        for (String hlsFile : hlsFiles) {
            File file = new File(hlsFile);
            try (InputStream in = new FileInputStream(file)) {
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(in.available());
                meta.setContentType("application/octet-stream");
                String key = folder + hlsFile;
                s3Client.putObject(new PutObjectRequest(
                        bucket, key, in, meta)
                        .withCannedAcl(CannedAccessControlList.Private));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Done with uploading HLS files to S3 for content ID {}", contentId);
        cleanup(toDelete);
    }

    private void cleanup(List<String> toDelete) {
        for (String path : toDelete) {
            File file = new File(path);
            file.delete();
        }
    }

    @Override
    public String getObject(String bucket, String key) {
        try {
            S3Object o = s3Client.getObject(bucket, key);
            S3ObjectInputStream s3is = o.getObjectContent();
            return new String(s3is.readAllBytes());
        } catch (IOException | AmazonServiceException e) {
            log.error("Error while fetching S3 object with key {}: {}", key, e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public CopyObjectResult copyObject(String srcBucket, String srcKey, String dstBucket, String dstKey) {
        return s3Client.copyObject(srcBucket, srcKey, dstBucket, dstKey);
    }

    @Override
    public void deleteObject(String bucket, String key) {
        s3Client.deleteObject(bucket, key);
    }
}
