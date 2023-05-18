package com.api.bigdata.api.dto;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class BookPreViewResponse {
    String doc_name, kdc_label, publisher, doc_id;

    public BookPreViewResponse(JSONObject jsonObject) {
        this.doc_name = jsonObject.get("doc_name").toString();
        this.kdc_label = jsonObject.get("kdc_label").toString();
        this.publisher = jsonObject.get("publisher").toString();
        this.doc_id = jsonObject.get("_id").toString();
    }
}
