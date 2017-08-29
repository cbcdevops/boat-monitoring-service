package com.cb.plcreader.controllers;


import com.cb.plcreader.models.AssetInformation;
import com.cb.plcreader.services.SocketClient;
import com.cloudant.client.api.Database;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    @RequestMapping("/write")
    public void write(){
        assetInformation.setAssetAbbreviation("AOS");
        assetInformation.setAssetName("M/V CHOCTAW");
        assetInformation.set_id("971260");
        assetInformation.setCronScheduleString("0 0 * * * *");
        assetInformation.setHostAddress1("127.0.0.1");
        assetInformation.setHostAddress2("127.0.0.1");
        assetInformation.setPort(2101);
        assetInformation.setReadSensorList(new String[]{"S0001","S0002","S0003"});
        database.save(assetInformation);
        assetInformation.setAssetAbbreviation("OOS");
        assetInformation.setAssetName("M/V EUGENIE J HUGER");
        assetInformation.set_id("1242009");
        assetInformation.setCronScheduleString("0 0 * * * *");
        assetInformation.setHostAddress1("127.0.0.1");
        assetInformation.setHostAddress2("127.0.0.1");
        assetInformation.setPort(2101);
        assetInformation.setReadSensorList(new String[]{"S0004","S0005","S0006"});
        database.save(assetInformation);
        assetInformation.setAssetAbbreviation("FOS");
        assetInformation.setAssetName("M/V EUGENIE P JONES");
        assetInformation.set_id(String.valueOf(562115));
        assetInformation.setCronScheduleString("0 0 * * * *");
        assetInformation.setHostAddress1("127.0.0.1");
        assetInformation.setHostAddress2("127.0.0.1");
        assetInformation.setPort(2101);
        assetInformation.setReadSensorList(new String[]{"S0007","S0002","S0001"});
        database.save(assetInformation);
    }

}
