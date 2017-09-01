package com.cb.plcreader.controller.tasks;

import com.cb.plcreader.controller.models.AssetInfo;
import org.springframework.web.client.RestTemplate;

import java.util.TimerTask;

public class QueryTask extends TimerTask {


    private AssetInfo assetInfo;

    private RestTemplate restTemplate = new RestTemplate();

    public QueryTask(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }


    @Override
    public void run() {
        restTemplate.postForObject("http://localhost:8090/dev/plcexecutor/read", assetInfo, String.class);

    }
}
