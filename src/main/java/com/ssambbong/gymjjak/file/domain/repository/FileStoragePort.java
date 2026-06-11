package com.ssambbong.gymjjak.file.domain.repository;

public interface FileStoragePort {

    String generatePresignedUploadUrl(String key, String contentType);

    String getPublicUrl(String key);

    String getPresignedUrl(String key);

    void delete(String key);
}
