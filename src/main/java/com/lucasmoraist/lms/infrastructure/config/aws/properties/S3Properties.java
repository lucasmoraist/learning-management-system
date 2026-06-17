package com.lucasmoraist.lms.infrastructure.config.aws.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String bucketName;

}
