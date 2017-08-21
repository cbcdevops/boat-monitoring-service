package com.cb.plcreader;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BatchtestApplication {


    public static void main(String args[]) throws Exception {
        SpringApplication.run(BatchtestApplication.class, args);
    }

    @Bean
    public Database mydb(CloudantClient cloudant) {
        return cloudant.database("capturedata", true);
    }



}