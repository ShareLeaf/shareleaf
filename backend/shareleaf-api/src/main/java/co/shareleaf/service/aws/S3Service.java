package co.shareleaf.service.aws;


import com.amazonaws.services.s3.model.CopyObjectResult;

import java.io.InputStream;

/**
 * @author Bizuwork Melesse
 * created on 2/13/22
 *
 * Provides CRUD services for interacting with objects in S3 buckets.
 */
public interface S3Service {
    String AUDIO_TYPE = "audio/mpeg";
    String VIDEO_TYPE = "video/mpeg";
    String IMAGE_TYPE = "image/jpeg";
    String GIF_TYPE = "image/gif";

    /**
     * Upload an object to S3
     *
     * @param bucket profile-specific artifact bucket
     * @param key filename
     * @param stream a JSON string
     * @param contentType
     * @return
     */
    void uploadImage(String bucket, String key, InputStream stream, String contentType);

    /**
     * Upload HLS segments and remove any local copies
     *  @param bucket
     * @param contentId
     * @param permalink
     */
    void uploadHlsData(String bucket, String contentId, String permalink);

    /**
     * Get any object specified by the bucket and key
     *
     * @param bucket
     * @param key
     * @return
     */
    String getObject(String bucket, String key);

    CopyObjectResult copyObject(String srcBucket, String srcKey, String dstBucket, String dstKey);

    void deleteObject(String bucket, String key);
}
