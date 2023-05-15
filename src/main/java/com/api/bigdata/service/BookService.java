package com.api.bigdata.service;

import com.api.bigdata.api.BookTag;
import com.api.bigdata.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public JSONObject readOneBook(String id){
        return bookRepository.findBookById(id);
    }

    public List<JSONObject> readBooksByTag(BookTag tag){
        String strTag = tagMatching(tag);
        if(strTag==null){
            return bookRepository.findBooksByKdcCode("810");
        }
        else {
            return bookRepository.findBooksByKdcCode(strTag);
        }
    }

    private String tagMatching(BookTag tag){
        switch (tag){
            case art -> {
                return "600";
            }
            case social -> {
                return "300";
            }
            case technology -> {
                return "500";
            }
            default ->{
                return null;
            }
        }
    }

}
