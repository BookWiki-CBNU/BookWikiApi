package com.api.bigdata.repository;

import org.json.simple.JSONObject;

import java.util.List;

public interface BookRepository {

    JSONObject findBookById(String id);

    List<JSONObject> findBooksByKdcCode(String code);

    List<JSONObject> findBooksByExcludeKdcCode(List<String> codes);
}
