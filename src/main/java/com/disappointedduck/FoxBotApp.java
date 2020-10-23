package com.disappointedduck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@EnableScheduling
public class FoxBotApp {
    public static void main(String[] args){
        SpringApplication.run(FoxBotApp.class, args);
    }


}
