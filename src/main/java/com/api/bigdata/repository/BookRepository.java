package com.api.bigdata.repository;

import org.json.simple.JSONObject;

import java.util.List;

public interface BookRepository {

    JSONObject findBookById(String id);
    byte[] getImageById(String id);

    List<JSONObject> findBookPreViewsByKdcCodeAndBookName(String bookName,String code);

    List<JSONObject> findBookPreViewsByBookNameAndExcludeKdcCodes(String BookName,List<String> codes);

    List<JSONObject> findBookPreViewsByPublisher(String publisher);

    List<JSONObject> countBooksByCode();
    List<JSONObject> countBooksByCode(List<String> codes);
    List<JSONObject> getRandomBooks();
    List<JSONObject> findDetailsByDocId(String docId);


}
