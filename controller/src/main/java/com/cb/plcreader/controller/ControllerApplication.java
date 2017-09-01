package com.cb.plcreader.controller;

import com.cb.plcreader.controller.services.ControllerService;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
@EnableScheduling
public class ControllerApplication {

    @Value("${cloudant.url}")
    private String couchURL;

    @Value("${cloudant.username}")
    private String couchAPIkey;

    @Value("${cloudant.password}")
    private String couchAPIKeyPassphrase;

    @Value("${cloudant.dataBase}")
    private String couchdataBase;

    @Autowired
    private ControllerService controllerService;

    public static void main(String[] args) {
        SpringApplication.run(ControllerApplication.class, args);

    }

    @Bean
    public Database mydb() {

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
