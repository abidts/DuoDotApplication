package com.duodot.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class ApplicationConfig {
    
    @Value("${application.aws.s3.region}")
    private String region;
    
    @Value("${application.aws.s3.access-key}")
    private String accessKey;
    
    @Value("${application.aws.s3.secret-key}")
    private String secretKey;

    @Value("${application.aws.sns.region}")
    private String snsRegion;

    @Value("${application.aws.sns.access-key}")
    private String snsAccessKey;

    @Value("${application.aws.sns.secret-key}")
    private String snsSecretKey;
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public S3Client s3Client() {
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            return null;
        }
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        if (snsAccessKey == null || snsAccessKey.isEmpty() || snsSecretKey == null || snsSecretKey.isEmpty()) {
            return null;
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(snsAccessKey, snsSecretKey);
        return SnsClient.builder()
                .region(Region.of(snsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
