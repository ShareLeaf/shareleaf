package co.shareleaf.service.aws;


import co.shareleaf.props.AWSProps;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> hlsFiles = getSortedHlsFiles(pathnames, contentId);
        List<String> toDelete = getFilesToDelete(pathnames, contentId);

        String folder = contentId + "/";
        log.info("Uploading HLS files to S3 for content ID {}", contentId);
        List<Mono<Boolean>> uploadTasks = new ArrayList<>();
        for (String hlsFile : hlsFiles) {
            uploadTasks.add(uploadTask(hlsFile, folder, bucket));
        }
        if (!uploadTasks.isEmpty()) {
            Flux.merge(uploadTasks)
                    .doOnError(e -> log.error(e.getLocalizedMessage()))
                    .doOnComplete(() -> {
                        log.info("Done with uploading HLS files to S3 for content ID {}", contentId);
                        cleanup(toDelete);
            }).subscribe();
        }
    }

    private List<String> getFilesToDelete(String[] pathnames, String contentId) {
        List<String> files = new ArrayList<>();
        if (pathnames != null) {
            for (String p : pathnames) {
                if (p.contains(contentId)) {
                    // Add all files (HLS and MP4) for deletion
                    files.add(p);
                }
            }
        }
        return files;
    }

    private List<String> getSortedHlsFiles(String[] pathnames, String contentId) {
        // Sort the HLS files so that the index file gets uploaded first and the
        // actual content gets uploaded in order. That way, a user may start streaming
        // the content before the uploading is finished.
        String indexFile = "";
        List<String> files = new ArrayList<>();
        if (pathnames != null) {
            for (String p : pathnames) {
                if (p.contains(contentId)) {
                    if (p.contains(".ts")) {
                        files.add(p);
                    } else if (p.contains(".m3u8")) {
                        indexFile = p;
                    }
                }
            }
        }
        // Sort the file names so that the numerical suffixes also
        // get sorted in order
        files.sort(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return extractInt(o1) - extractInt(o2);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        files.add(0, indexFile);
        return files;
    }

    private Mono<Boolean> uploadTask(String hlsFile, String folder, String bucket) {
        File file = new File(hlsFile);
        try (InputStream in = new FileInputStream(file)) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(in.available());
            meta.setContentType("application/octet-stream");
            String key = folder + hlsFile;
            log.info("Uploading HLS file to S3: {}", key);
            s3Client.putObject(new PutObjectRequest(
                    bucket, key, in, meta)
                    .withCannedAcl(CannedAccessControlList.Private));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Mono.just(true);
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


