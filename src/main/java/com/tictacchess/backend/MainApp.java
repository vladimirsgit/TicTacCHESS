package com.tictacchess.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Timestamp;
import java.time.Instant;

@EnableScheduling
@SpringBootApplication
public class MainApp {

    public static void main(String[] args) {

        SpringApplication.run(MainApp.class, args);

    }

}
