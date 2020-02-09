package com.jinseonkim.photocloud;

import com.jinseonkim.photocloud.storage.StorageProperties;
import com.jinseonkim.photocloud.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class PhotoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoCloudApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}

}
