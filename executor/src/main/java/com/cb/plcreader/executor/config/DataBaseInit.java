package com.cb.plcreader.executor.config;


import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DataBaseInit {

    @Autowired
    Database database;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void initDB() {
        this.createIndex();
    }

    /**
     * @Description Create the required Indexes
     */
    private void createIndex() {

        database.createIndex("findbyDocType", "findbyDocType", "json", new IndexField[]{new IndexField("docType", IndexField.SortOrder.asc)});

    }
}
