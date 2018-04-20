package com.code10.xml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PublishingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublishingSystemApplication.class, args);
    }
}
