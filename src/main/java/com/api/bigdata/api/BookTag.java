package com.api.bigdata.api;

import lombok.Getter;

public enum BookTag {
    technology("500"),
    social("300"),
    art("600"),
    other("-1");

    @Getter
    private final  String code;
    BookTag(String code) {
        this.code = code;
    }
}
