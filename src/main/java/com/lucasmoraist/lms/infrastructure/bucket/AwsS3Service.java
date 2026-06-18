package com.lucasmoraist.lms.infrastructure.bucket;

import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.infrastructure.config.aws.properties.S3Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@Service
public class AwsS3Service implements BucketGateway {

    private final S3Properties s3Properties;
    private final S3Client s3Client;

    public AwsS3Service(S3Properties s3Properties, S3Client s3Client) {
        this.s3Properties = s3Properties;
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(String key, MultipartFile video) {
        log.debug("Uploading file to S3 with key: {}", key);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .contentType(video.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(video.getInputStream(), video.getSize()));

            log.debug("File uploaded successfully to S3 with key: {}", key);
        } catch (IOException ex) {
            log.error("[{}] - Error uploading video to Supabase Storage: {}", key, ex.getMessage());
            throw new RuntimeException("Failed to upload video", ex);
        }
    }

}
