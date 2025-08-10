package com.example.meetingroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MeetingroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingroomApplication.class, args);
    }

}
