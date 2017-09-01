package com.cb.plcreader.executor.controllers;


import com.cb.plcreader.executor.services.SocketService;
import com.cloudant.client.api.Database;
import com.cb.plcreader.executor.models.AssetInformation;
import com.cb.plcreader.executor.models.CaptureInformation;
import com.cb.plcreader.executor.models.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
public class ExecController {

    @Autowired
    private SocketService socketService;

    @Autowired
    private Database database;

    @Autowired
    private AssetInformation assetInformation;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ApplicationContext appContext;


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @RequestMapping("/getassetdocs")
    public List<AssetInformation> readValue() {

        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        return database.findByIndex(selectorJson, AssetInformation.class);
    }

    @GetMapping
    @RequestMapping("/getcapdocs")
    public List<CaptureInformation> getcapdocs() {

        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"capture_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        return database.findByIndex(selectorJson, CaptureInformation.class);
    }

    @PostMapping
    @RequestMapping("/read")
    public AssetInformation readValue(@RequestBody AssetInformation assetInformation) {
        Globals.assetsToProcess.push(assetInformation);
        threadPoolTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                log.info("Querying process for asset " + assetInformation.getAssetName() + " started at " + new Date().toString());
                appContext.getBean(SocketService.class).run();
                socketService.run();
                log.info("Querying process for asset " + assetInformation.getAssetName() + " ended at " + new Date().toString());
            }
        });
        return assetInformation;
    }

    @GetMapping
    @RequestMapping("/readall")
    //Todo - Implement the logic to triggger the read from a control program.
    public String readValuesOfAll() {
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        List<AssetInformation> assetDocs = database.findByIndex(selectorJson, AssetInformation.class);
        for (AssetInformation assetDoc : assetDocs) {
            Globals.assetsToProcess.push(assetDoc);
            threadPoolTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    log.info("Querying process for asset " + assetDoc.getAssetName() + " started at " + new Date().toString());
                    appContext.getBean(SocketService.class).run();
                    log.info("Querying process for asset " + assetDoc.getAssetName() + " ended at " + new Date().toString());
                }
            });
        }
        return null;
    }
}
