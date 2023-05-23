package com.api.bigdata.service;

import com.api.bigdata.api.BookTag;
import com.api.bigdata.api.dto.bookCount.BookCountResponse;
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

    public List<JSONObject> getBooksListByTag(BookTag tag){
        if(tag.equals(BookTag.other)){
            return bookRepository.findBookPreViewsByBookNameAndExcludeKdcCodes(null,
                    new ArrayList<>(Arrays.asList(
                            BookTag.art.getCode(), BookTag.technology.getCode(), BookTag.social.getCode()
                    )));
        }
        else {
            return bookRepository.findBookPreViewsByKdcCodeAndBookName(null,tag.getCode());
        }
    }

    public List<JSONObject> getBooksListByPublisher(String publisher){
        return bookRepository.findBookPreViewsByPublisher(publisher);
    }

    public BookCountResponse countBooks(){
        List<JSONObject> counts = bookRepository.countBooksByCode();

        return new BookCountResponse(counts);
    }


    public List<JSONObject> readRandomBooks(){
        return bookRepository.getRandomBooks();
    }


    public List<JSONObject> readBookDetailByDocId(String docId){
        return bookRepository.findDetailsByDocId(docId);
    }


    public List<JSONObject> getBooksListByBookNameAndTag(String bookName,BookTag tag){
        if(tag.equals(BookTag.other)){
            return bookRepository.findBookPreViewsByBookNameAndExcludeKdcCodes(bookName,
                    new ArrayList<>(Arrays.asList(
                            BookTag.art.getCode(), BookTag.technology.getCode(), BookTag.social.getCode()
                    )));
        }
        else {
            return bookRepository.findBookPreViewsByKdcCodeAndBookName(bookName,tag.getCode());
        }
    }

    public byte[] getImageById(String id){
        return bookRepository.getImageById(id);
    }

}
