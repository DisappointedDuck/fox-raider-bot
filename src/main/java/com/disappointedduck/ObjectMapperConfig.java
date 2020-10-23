package com.disappointedduck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ObjectMapperConfig {

    @Bean
    @Primary
    ObjectMapper objectMapper(){
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
    }
}
