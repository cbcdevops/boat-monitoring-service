package com.cb.plcreader.controller.services;

import com.cb.plcreader.controller.models.AssetInfo;
import com.cb.plcreader.controller.tasks.QueryTask;
import com.cloudant.client.api.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Component
@RestController
public class ControllerService {


    @Autowired
    Database database;

    private HashMap<String,Timer> timerInfo = new HashMap<String,Timer>();


    @GetMapping
    @RequestMapping("/start")
    public String startProcessing() {

        if (timerInfo.size() > 0)
            return "Polling process on assets are already running!";

        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        timerInfo.clear();

        List<AssetInfo> asetInfoList = database.findByIndex(selectorJson, AssetInfo.class);
        if (asetInfoList.size() > 0) {
            asetInfoList.forEach(assetInfo -> {
                Timer t = new Timer();
                t.scheduleAtFixedRate(new QueryTask(assetInfo), new Date(), Integer.parseInt(assetInfo.getRunFreqInSec()) * 100);
                timerInfo.put(assetInfo.get_id(),t);
            });
        }

        return "Interval polling process started for " + timerInfo.size() + " assets";

    }

    @GetMapping
    @RequestMapping("/stop")
    public String stopProcessing() {

        if (timerInfo.size() == 0)
            return "No polling process running!";

        Iterator itr = timerInfo.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry pair = (Map.Entry)itr.next();
            ((Timer) pair.getValue()).cancel();
            itr.remove();
        }
        timerInfo.clear();
        return "Polling process on all assets has been stopped!";

    }
}
