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

    public BookDetailResponse(List<JSONObject> jsonObjects) {
        JSONObject metadata = (JSONObject) jsonObjects.get(0).get("metadata");
        this.image = toString(getOId((JSONObject) jsonObjects.get(0).get("image")));
        this.docId = toString(metadata.get("doc_id"));
        this.docName = toString(metadata.get("doc_name"));
        this.author = toString(metadata.get("author"));
        this.publisher = toString(metadata.get("publisher"));
        this.kdcLabel = toString(metadata.get("kdc_label"));
        this.summaryList = new ArrayList<>();

        for(JSONObject jsonObject:jsonObjects){
            if(jsonObject.get("summary")!=null)
                summaryList.add(jsonObject.get("summary").toString());
        }
    }

    private String toString(Object data){
        if(data==null) return null;
        else return data.toString();
    }

    private Object getOId(JSONObject data){
        System.out.println("data = " + data);
        if(data==null) return null;
        else return data.get("$oid");
    }
}
