package com.cb.plcreader.executor.schedulers;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class readScheduler {

    //@Scheduled(cron="*/30 * * * * *")
    @Scheduled(cron="0 0 * * * *")
    public void readDataforEverySecond() {
    }
}
