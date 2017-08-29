package com.cb.plcreader.controllers;


import com.cb.plcreader.models.AssetInformation;
import com.cb.plcreader.services.SocketClient;
import com.cloudant.client.api.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ReadController {

    @Autowired
    private SocketClient socketClient;

    @Autowired
    private Database database;

    @Autowired
    private AssetInformation assetInformation;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @GetMapping
    @RequestMapping("/read")
    public List<AssetInformation> readValue() {

/*        return database.search("_design/findbyDocType")
                .limit(10)
                .includeDocs(true)
                .query("*:*", SensorInfo.class);*/


        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
       return  database.findByIndex(selectorJson, AssetInformation.class);

    }

    @GetMapping
    @RequestMapping("/start")
    public String readValues() {
        Long startTime =  System.currentTimeMillis();
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        List<AssetInformation> assetDocs = database.findByIndex(selectorJson, AssetInformation.class);
        for (AssetInformation assetDoc : assetDocs) {
            socketClient.readAssetRegisters(assetDoc);

        }
        return String.format("Total time to query " + assetDocs.size() + " assets is " + (System.currentTimeMillis() - startTime) + "ms");
    }

}
