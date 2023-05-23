package com.api.bigdata.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongodbConnector {

    @Value("${data.path.url}")
    private String ip;
    private MongoDatabase db;

    public MongoCollection<Document> getCollection(String name){
        if(db==null){
            MongoClient mongoClient = MongoClients.create(ip);
            db = mongoClient.getDatabase("bigdata");
        }
        return db.getCollection(name);
    }

    public GridFSBucket getGridFSBucket(){
        if(db==null){
            MongoClient mongoClient = MongoClients.create(ip);
            db = mongoClient.getDatabase("bigdata");
        }
        return GridFSBuckets.create(db);
    }
}
