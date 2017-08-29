package com.cb.plcreader.config;

import com.cb.plcreader.models.SensorInfo;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.Response;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class DataBaseInit {

    @Autowired
    Database database;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void initDB() {


        //Create the Indexes if not present
        this.createIndex();

        //Create the Sensors information if not present.
        try {
            this.initSensorInfoCollection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initSensorInfoCollection() throws IOException {
        String selectorJson = "{\"selector\":{\"docType\":{\"$eq\":\"sensor_info\"}},\"fields\":[\"_id\",\"_rev\"],\"sort\":[{\"docType\":\"asc\"}]}";
        if (database.findByIndex(selectorJson, SensorInfo.class).isEmpty()) {
            CsvMapper mapper = new CsvMapper();
            CsvSchema sensorSchema =  mapper.schemaWithHeader();
            File sensorListFile = new ClassPathResource("sensorlist.csv").getFile();
            MappingIterator<SensorInfo> it = mapper.readerFor(SensorInfo.class)
                    .with(sensorSchema)
                    .readValues(sensorListFile);
            int sum = 0;
            while (it.hasNext()) {
                Response response = database.save(it.next());
                sum++;
            }
            log.info("Sensors Information Updated");
            log.info(sum + " SensorInfo details inserted into DataBase");
        }
    }

    private void createIndex() {

        database.createIndex("findbyDocType", "findbyDocType", "json", new IndexField[]{new IndexField("docType", IndexField.SortOrder.asc)});

        //Universal Index using
        //database.createIndex("{\"index\":{\"fields\":[\"docType\"]},\"type\":\"text\"}");

        //database.createIndex("findbySensorGroup-index", "findbySensorGroup-index", "json", new IndexField[]{new IndexField("docType", IndexField.SortOrder.asc),new IndexField("sensorGroup", IndexField.SortOrder.asc)});
    }
}
