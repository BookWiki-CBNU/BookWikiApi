package com.api.bigdata.api.dto;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.List;

@Data
public class BookCountResponse {
    Long technology=0L;
    Long social=0L;
    Long art=0L;
    Long other=0L;

    public BookCountResponse(List<JSONObject> jsonObjects) {
        for(JSONObject jsonObject:jsonObjects){
            switch (jsonObject.get("_id").toString()) {
                case "600" -> this.art += (Long) jsonObject.get("count");
                case "500" -> this.technology += (Long) jsonObject.get("count");
                case "300" -> this.social += (Long) jsonObject.get("count");
                default -> this.other += (Long) jsonObject.get("count");
            }
        }
    }
}
