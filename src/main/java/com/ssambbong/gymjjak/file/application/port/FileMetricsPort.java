package com.ssambbong.gymjjak.file.application.port;

public interface FileMetricsPort {

    void recordPresignedUrlGenerated();

    void recordFileRegistered();

    void recordFileDeleted();
}
