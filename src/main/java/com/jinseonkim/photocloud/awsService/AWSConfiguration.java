package com.jinseonkim.photocloud.awsService;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class AWSConfiguration implements WebMvcConfigurer {
    @Value("AKIARI7I35SVOYOCQOYH")
    private String accessKey;

    @Value("8R/rimRYf1IEhDHHxdupgVw4Q6D3QFmXBlMAc2vR")
    private String secretKey;

    @Bean
    public BasicAWSCredentials awsCredentials() {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        return creds;
    }

    @Bean
    public AmazonS3 awsS3Client() {
        AmazonS3 s3Builder = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials()))
                .build();
        return s3Builder;
    }
}
