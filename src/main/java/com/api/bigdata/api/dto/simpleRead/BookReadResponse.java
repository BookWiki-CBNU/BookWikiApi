package com.api.bigdata.api.dto.simpleRead;

import lombok.Data;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Data
public class BookReadResponse {
    JSONObject doc;

    public BookReadResponse(JSONObject doc) {
        this.doc = doc;
    }

}
