package com.lucasmoraist.lms.domain.gateway;

import org.springframework.web.multipart.MultipartFile;

public interface BucketGateway {
    void uploadFile(String key, MultipartFile file);
    void uploadBytes(String key, byte[] content, String contentType);
    String getPublicUrl(String key);
}
