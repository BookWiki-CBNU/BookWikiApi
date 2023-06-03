package com.api.bigdata.api.dto.bookDetail;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookDetailResponse {
    String image;
    String docId;
    String docName;
    String author;
    String publisher;
    String kdcLabel;

    List<String> summaryList;

    public BookDetailResponse(List<JSONObject> jsonObjects,String imagePrefix) {
        JSONObject data = jsonObjects.get(0);
        this.image = toString(getOId((JSONObject) jsonObjects.get(0).get("image"),imagePrefix));
        this.docId = toString(data.get("doc_id"));
        this.docName = toString(data.get("doc_name"));
        this.author = toString(data.get("author"));
        this.publisher = toString(data.get("publisher"));
        this.kdcLabel = toString(data.get("kdc_label"));
        this.summaryList = (List<String>) data.get("summary");
    }

    private String toString(Object data){
        if(data==null) return null;
        else return data.toString();
    }

    private Object getOId(JSONObject data,String prefix){
        if(data==null) return null;
        else return prefix+data.get("$oid");
    }
}
