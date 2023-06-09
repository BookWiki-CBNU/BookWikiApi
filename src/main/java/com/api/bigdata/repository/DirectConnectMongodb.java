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

        query.append("metadata.kdc_code", code);
        if(bookName!=null&&!bookName.isBlank()){
            query.append("metadata.doc_name", new Document("$regex",bookName));
        }
        getPreViews(query).into(documents);
        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> findBookPreViewsByBookNameAndExcludeKdcCodes(String bookName,List<String> codes) {
        List<Document> documents = new ArrayList<>();

        Document query = new Document();

        List<Document> conditions = new ArrayList<>();
        for (String code : codes) {
            Document condition = new Document("metadata.kdc_code", new Document("$ne", code));
            conditions.add(condition);
        }
        query.append("$and", conditions);
        if(bookName!=null&&!bookName.isBlank()){
            query.append("metadata.doc_name", new Document("$regex",bookName));
        }

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
    public List<JSONObject> countBooksByYear() {
        List<Document> pipeline = new ArrayList<>();

        pipeline.add(new Document("$match", new Document("metadata.kdc_code", new Document("$ne", null))
                .append("metadata.published_year", new Document("$ne", null))));

        pipeline.add(new Document("$group", new Document("_id", "$metadata.doc_id")
                .append("kdc_code", new Document("$first", "$metadata.kdc_code"))
                .append("published_year", new Document("$first", "$metadata.published_year"))));


        pipeline.add(new Document("$group", new Document("_id",
                new Document("$cond", Arrays.asList(
                        new Document("$lt", Arrays.asList("$published_year", "2014")),
                        "2013~",
                        "$published_year"
                )))
                        .append("500", new Document("$sum",
                                new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$kdc_code", "500")), 1, 0))))
                        .append("300", new Document("$sum",
                                new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$kdc_code", "300")), 1, 0))))
                        .append("600", new Document("$sum",
                                new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$kdc_code", "600")), 1, 0))))
                        .append("other", new Document("$sum",
                                new Document("$cond", Arrays.asList(
                                        new Document("$not", new Document("$in", Arrays.asList("$kdc_code", Arrays.asList("600", "500", "300")))),
                                        1,
                                        0))))
                        .append("total", new Document("$sum", 1))
                ));
        pipeline.add(new Document("$sort", new Document("_id", 1)));

        List<Document> documents = new ArrayList<>();
        collection.aggregate(pipeline).into(documents);
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
    public List<JSONObject> countBooksByCode(List<String> codes) {
        Document condition = new Document("_id", null);

        List<Document> condExpressions = new ArrayList<>();
        for(String code:codes){

            condition.append(code, new Document("$sum", new Document("$cond",
                    Arrays.asList(new Document("$eq", Arrays.asList("$metadata.kdc_code", code)), 1, 0))));

            condExpressions.add(new Document("$ne", Arrays.asList("$metadata.kdc_code", code)));
        }

        Document otherCondition = new Document("$and", condExpressions);
        Document otherSumExpression = new Document("$cond", Arrays.asList(otherCondition, 1, 0));
        Document otherField = new Document("$sum", otherSumExpression);

        condition.append("other", otherField);


        condition.append("total", new Document("$sum", 1));

        Document query = new Document("$group", condition);

        List<Document> documents = new ArrayList<>();
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

        List<Document> pipeline = Arrays.asList(
                new Document("$match", new Document("metadata.doc_id", docId)),
                new Document("$group", new Document("_id", "$metadata.doc_id")
                        .append("doc_id", new Document("$first", "$metadata.doc_id"))
                        .append("image", new Document("$first", "$image"))
                        .append("doc_name", new Document("$first", "$metadata.doc_name"))
                        .append("author", new Document("$first", "$metadata.author"))
                        .append("publisher", new Document("$first", "$metadata.publisher"))
                        .append("kdc_label", new Document("$first", "$metadata.kdc_label"))
                        .append("summary", new Document("$push", "$summary")))
        );

        List<Document> documents = new ArrayList<>();

        collection.aggregate(pipeline).into(documents);

        return documentsToJSONObject(documents);
    }

    @Override
    public List<JSONObject> getTopGradesByDocument() {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", new Document("grade", new Document("$exists", true))));
        pipeline.add(new Document("$group", new Document("_id", "$metadata.doc_id")
                .append("grade", new Document("$avg", "$grade"))
                .append("doc_name", new Document("$first", "$metadata.doc_name"))
                .append("publisher", new Document("$first", "$metadata.publisher"))
                .append("kdc_label", new Document("$first", "$metadata.kdc_label"))));
        pipeline.add(new Document("$sort", new Document("grade", -1)));
        pipeline.add(new Document("$limit", 3));

        List<Document> documents = new ArrayList<>();
        collection.aggregate(pipeline).into(documents);
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
