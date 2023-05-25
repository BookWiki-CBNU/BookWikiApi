package com.api.bigdata;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Config {

    @Value("${image.path.url}")
    private String imageUrl;
}
