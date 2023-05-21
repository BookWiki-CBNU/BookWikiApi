package com.api.bigdata.repository;

import org.json.simple.JSONObject;

import java.util.List;

public interface BookRepository {

    JSONObject findBookById(String id);

    List<JSONObject> findBookPreViewsByKdcCode(String code);

    List<JSONObject> findBookPreViewsByExcludeKdcCode(List<String> codes);

    List<JSONObject> countBooksByCode();
    List<JSONObject> getRandomBooks();
    List<JSONObject> findDetailsByDocId(String docId);


}
