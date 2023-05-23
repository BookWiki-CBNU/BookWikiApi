package com.api.bigdata.repository;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class DirectConnectMongodb implements BookRepository{

    private final JSONParser jsonParser;
    private final MongoCollection<Document> collection;
    private final GridFSBucket gridFSBucket;

    public DirectConnectMongodb(MongodbConnector mongodbConnector) {
        collection = mongodbConnector.getCollection("books");
        jsonParser = new JSONParser();
        gridFSBucket= mongodbConnector.getGridFSBucket();
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
    public byte[] getImageById(String id) {
        // 가져올 사진의 ObjectId
        ObjectId photoId = new ObjectId(id);

        // 사진 가져오기
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        gridFSBucket.downloadToStream(photoId, outputStream);


        // 가져온 이미지를 Spring으로 전달
        byte[] imageBytes = outputStream.toByteArray();
        return imageBytes;
    }

    @Override
    public List<JSONObject> findBookPreViewsByKdcCodeAndBookName(String bookName,String code) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document();

        if(bookName!=null&&!bookName.isBlank()){
            query.append("metadata.doc_name", new Document("$regex",bookName));
        }

        query.append("metadata.kdc_code", code);
        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> findBookPreViewsByBookNameAndExcludeKdcCodes(String bookName,List<String> codes) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document();

        if(bookName!=null&&!bookName.isBlank()){
            query.append("metadata.doc_name", new Document("$regex",bookName));
        }


        List<Document> conditions = new ArrayList<>();
        for (String code : codes) {
            Document condition = new Document("metadata.kdc_code", new Document("$ne", code));
            conditions.add(condition);
        }
        query.append("$and", conditions);
        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> findBookPreViewsByPublisher(String publisher) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document("metadata.publisher", new Document("$regex",publisher));
        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> countBooksByCode() {
        List<Document> documents = new ArrayList<>();

        Document query = new Document("$group",
                new Document("_id", "$metadata.kdc_code")
                        .append("count", new Document("$sum", 1)));

        collection.aggregate(Arrays.asList(query)).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> getRandomBooks() {
        List<Document> documents = new ArrayList<>();



        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$metadata.doc_id")
                        .append("doc_name", new Document("$first", "$metadata.doc_name"))
                        .append("publisher", new Document("$first", "$metadata.publisher"))
                        .append("kdc_label", new Document("$first", "$metadata.kdc_label"))),
                new Document("$sample", new Document("size", 3))
        );


        collection.aggregate(pipeline).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> findDetailsByDocId(String  docId) {
        List<Document> documents = new ArrayList<>();
        Document query = new Document("metadata.doc_id", docId);

        collection.find(query).into(documents);

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
