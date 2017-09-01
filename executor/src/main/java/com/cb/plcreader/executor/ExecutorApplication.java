package com.cb.plcreader.executor;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class ExecutorApplication {

    @Value("${cloudant.url}")
    private String couchURL;

    @Value("${cloudant.username}")
    private String couchAPIkey;

    @Value("${cloudant.password}")
    private String couchAPIKeyPassphrase;

    @Value("${cloudant.dataBase}")
    private String couchdataBase;

    @Value("${thread.corepoolsize}")
    private int threadCorePoolSize;

    @Value("${thread.maxpoolsize}")
    private int threadMaxPoolSize;

    @Value("${thread.queuecapacity}")
    private int threadQueuSize;

    public static void main(String[] args) {
        SpringApplication.run(ExecutorApplication.class, args);
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

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(threadCorePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(threadMaxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(threadQueuSize);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setKeepAliveSeconds(120);
        return threadPoolTaskExecutor;
    }
}
