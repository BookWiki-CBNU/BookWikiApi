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
        if(tag.equals(BookTag.other)){
            return bookRepository.findBookPreViewsByExcludeKdcCode(
                    new ArrayList<>(Arrays.asList(
                            BookTag.art.getCode(), BookTag.technology.getCode(), BookTag.social.getCode()
                    )));
        }
        else {
            return bookRepository.findBookPreViewsByKdcCode(tag.getCode());
        }
    }

    public BookCountResponse countBooks(){
        List<JSONObject> counts = bookRepository.countBooksByCode();

        return new BookCountResponse(counts);
    }


}
