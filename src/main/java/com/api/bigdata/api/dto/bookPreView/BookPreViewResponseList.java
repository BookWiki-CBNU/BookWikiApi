package com.api.bigdata.api.dto.bookPreView;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookPreViewResponseList {
    List<BookPreViewResponse> list;

    public BookPreViewResponseList(List<JSONObject> jsonObjects) {
        this.list = new ArrayList<>();
        for(JSONObject json:jsonObjects){
            this.list.add(new BookPreViewResponse(json));
        }
    }
}
