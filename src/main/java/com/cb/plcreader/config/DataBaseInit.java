package com.cb.plcreader.config;

import com.cb.plcreader.models.AssetInformation;
import com.cb.plcreader.models.SensorInfo;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class DataBaseInit {

    @Autowired
    Database database;

    @Value("${spring.profiles.active}")
    private String profile;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void initDB() {

        this.createIndex();
        try {
            this.initSensorInfoCollection();
            this.initAssetInfoCollection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Todo Mechanism to refresh the sensor information

    /**
     * @throws IOException
     * @description to insert the sesnr information into the database.
     * any udpate to sensor information programaticaly requires a new build and deploy.
     */
    private void initSensorInfoCollection() throws IOException {

        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"sensor_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        List<Object> response = database.findByIndex(selectorJson, Object.class);
        if (response.size() > 0) {
            response.forEach(sensorInfo -> {
                database.remove(sensorInfo);
            });
        }

        CsvMapper mapper = new CsvMapper();
        CsvSchema sensorSchema = mapper.schemaWithHeader();
        File sensorListFile = new ClassPathResource(profile + "/sensorlist.csv").getFile();
        MappingIterator<SensorInfo> it = mapper.readerFor(SensorInfo.class)
                .with(sensorSchema)
                .readValues(sensorListFile);
        int sum = 0;
        while (it.hasNext()) {
            database.save(it.next());
            sum++;
        }
        log.info("Sensors Information Updated");
        log.info(sum + " SensorInfo details inserted into DataBase");

    }

    /**
     * @throws IOException
     * @Description Refresh the asset information in the database.
     */
    private void initAssetInfoCollection() throws IOException {
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"asset_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        List<Object> response = database.findByIndex(selectorJson, Object.class);
        if (response.size() > 0) {
            response.forEach(assetInfo -> {
                database.remove(assetInfo);
            });
        }
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        CsvSchema sensorSchema = mapper.schemaWithHeader();
        File sensorListFile = new ClassPathResource(profile + "/assetinfo.csv").getFile();
        MappingIterator<AssetInformation> it = mapper.readerFor(AssetInformation.class)
                .with(sensorSchema)
                .readValues(sensorListFile);
        int sum = 0;
        while (it.hasNext()) {
            database.save(it.next());
            sum++;
        }
        log.info("Asset Information Updated");
        log.info(sum + " AssetInfo details inserted into DataBase");
    }

    /**
     * @Description Create the required Indexes
     */
    private void createIndex() {

        database.createIndex("findbyDocType", "findbyDocType", "json", new IndexField[]{new IndexField("docType", IndexField.SortOrder.asc)});

    }
}
