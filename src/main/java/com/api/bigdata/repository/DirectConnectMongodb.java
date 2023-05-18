package com.api.bigdata.repository;

import com.mongodb.client.*;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class DirectConnectMongodb implements BookRepository{

    private final JSONParser jsonParser;
    private final MongoCollection<Document> collection;

    public DirectConnectMongodb(MongodbConnector mongodbConnector) {
        collection = mongodbConnector.getCollection("books");
        jsonParser = new JSONParser();
    }

    JSONObject documentToJson(Document document){
        JSONObject res=null;
        try {
            res = (JSONObject) jsonParser.parse(document.toJson().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public JSONObject findBookById(String id) {
        Document document=collection.find().first();

        return documentToJson(document);
    }

    @Override
    public List<JSONObject> findBookPreViewsByKdcCode(String code) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document("metadata.kdc_code", code);
        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> findBookPreViewsByExcludeKdcCode(List<String> codes) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document();

        for(String code:codes){
            query.append("metadata.kdc_code", new Document("$ne", code));
        }

        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }


    private List<JSONObject> documentsToJSONObject(List<Document> documents){
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Document doc : documents) {
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonParser.parse(doc.toJson().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    private AggregateIterable<Document> getPreViews(Document match){
        // aggregate 파이프라인 작성
        List<Document> pipeline = Arrays.asList(
                new Document("$match", match),
                new Document("$group", new Document("_id", "$metadata.doc_id")
                        .append("doc_name", new Document("$first", "$metadata.doc_name"))
                        .append("publisher", new Document("$first", "$metadata.publisher"))
                        .append("kdc_label", new Document("$first", "$metadata.kdc_label"))),
                new Document("$limit", 20)
        );

        // aggregate 실행
        return collection.aggregate(pipeline);
    }


}
