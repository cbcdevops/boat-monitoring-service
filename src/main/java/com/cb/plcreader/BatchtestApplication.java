package com.cb.plcreader;

import com.cb.plcreader.util.client;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class BatchtestApplication {

    @Value("${cloudant.url}")
    private String couchURL;

    @Value("${cloudant.username}")
    private String couchAPIkey;

    @Value("${cloudant.password}")
    private String couchAPIKeyPassphrase;

    @Value("${cloudant.dataBase}")
    private String couchdataBase;

    public static void main(String args[]) throws Exception {
        SpringApplication.run(BatchtestApplication.class, args);
    }

    @Bean
    public Database mydb( ) {

        try {
            CloudantClient client = ClientBuilder.url(new URL(couchURL))
                    .username(couchAPIkey)
                    .password(couchAPIKeyPassphrase)
                    .build();
            return client.database(couchdataBase, true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
       return null;
    }


}