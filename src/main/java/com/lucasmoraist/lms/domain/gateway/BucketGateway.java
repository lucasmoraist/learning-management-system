package com.lucasmoraist.lms.domain.gateway;

import org.springframework.web.multipart.MultipartFile;

public interface BucketGateway {
    void uploadFile(String key, MultipartFile file);
}
