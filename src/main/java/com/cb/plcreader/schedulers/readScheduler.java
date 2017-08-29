package com.cb.plcreader.schedulers;

import com.cb.plcreader.services.SocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class readScheduler {

    @Autowired
    private SocketClient socketClient;

    //@Scheduled(cron="*/30 * * * * *")
    @Scheduled(cron="0 0 * * * *")
    public void readDataforEverySecond()
    {
        System.out.println("Hi Lokesh");
    }
}
