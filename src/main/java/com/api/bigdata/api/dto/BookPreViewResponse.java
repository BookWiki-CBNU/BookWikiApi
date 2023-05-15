package com.api.bigdata.api.dto;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class BookPreViewResponse {
    String doc_name, kdc_label, publisher, doc_id;

    public BookPreViewResponse(JSONObject jsonObject) {
        JSONObject metadata = (JSONObject) jsonObject.get("metadata");
        this.doc_name = metadata.get("doc_name").toString();
        this.kdc_label = metadata.get("kdc_label").toString();
        this.publisher = metadata.get("publisher").toString();
        this.doc_id = metadata.get("doc_id").toString();
    }
}
