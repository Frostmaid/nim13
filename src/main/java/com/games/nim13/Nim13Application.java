package com.games.nim13;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Nim13Application {

    public static void main(String[] args) {
        SpringApplication.run(Nim13Application.class, args);
    }

    @Bean
    public Module jacksonDataTypesModule() {
        return new Jdk8Module();
    }

    @Bean
    public Random random() {
        return new Random();
    }
}
