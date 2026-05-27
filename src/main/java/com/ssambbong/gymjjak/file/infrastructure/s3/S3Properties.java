package com.ssambbong.gymjjak.file.infrastructure.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
@Getter
@Setter
public class S3Properties {

    private String region;
    private Credentials credentials = new Credentials();
    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
    }
}
