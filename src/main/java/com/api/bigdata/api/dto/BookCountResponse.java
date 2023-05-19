package com.api.bigdata.api.dto;

import com.api.bigdata.api.BookTag;
import lombok.Data;
import org.json.simple.JSONObject;

import java.util.List;


@Data
public class BookCountResponse {
    Long technology=0L;
    Long social=0L;
    Long art=0L;
    Long other=0L;

    Long total=0L;

    public BookCountResponse(List<JSONObject> jsonObjects) {
        for(JSONObject jsonObject:jsonObjects){
            String id = jsonObject.get("_id").toString();
            Long count = (Long) jsonObject.get("count");
            this.total += count;
            if (BookTag.art.getCode().equals(id)) {
                this.art += count;
            }
            else if (BookTag.technology.getCode().equals(id)) {
                this.technology += count;
            }
            else if (BookTag.social.getCode().equals(id)) {
                this.social += count;
            }
            else {
                this.other += count;
            }

        }
    }
}
