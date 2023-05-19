package com.api.bigdata.service;

import com.api.bigdata.api.BookTag;
import com.api.bigdata.api.dto.BookCountResponse;
import com.api.bigdata.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
            return bookRepository.findBookPreViewsByExcludeKdcCode(new ArrayList<>(Arrays.asList("600", "300", "500")));
        }
        else {
            return bookRepository.findBookPreViewsByKdcCode(strTag);
        }
    }

    public BookCountResponse countBooks(){
        List<JSONObject> counts = bookRepository.countBooksByCode();

        return new BookCountResponse(counts);
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
