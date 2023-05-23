package com.api.bigdata.api;

import com.api.bigdata.api.dto.bookCount.BookCountResponse;
import com.api.bigdata.api.dto.bookPreView.BookPreViewResponseList;
import com.api.bigdata.api.dto.simpleRead.BookReadResponse;
import com.api.bigdata.api.dto.bookDetail.BookDetailResponse;
import com.api.bigdata.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookApi {
    private final BookService bookService;

    @Operation(summary = "테스트용도 api")
    @GetMapping("/test")
    public ResponseEntity<BookReadResponse> readBookOne(){
        JSONObject doc = bookService.readOneBook("");


        BookReadResponse bookReadResponse = new BookReadResponse(doc);
        return new ResponseEntity<>(bookReadResponse, HttpStatus.OK);
    }

    @Operation(summary = "카테고리로 책의 preview를 가져옴", description = "파라미터로 카테고리 입력시 해당하는 preview를 20개만 가져옴")
    @GetMapping("/read/{category}")
    public ResponseEntity<BookPreViewResponseList> readBookByCategory(@PathVariable("category") @Valid BookTag category){
        List<JSONObject> jsonObjects = bookService.getBooksListByTag(category);
        BookPreViewResponseList bookPreViewResponseList = new BookPreViewResponseList(jsonObjects);
        return new ResponseEntity<>(bookPreViewResponseList, HttpStatus.OK);
    }

    @Operation(summary = "각 카테고리에 해당하는 책의 개수를 반환",
            description = "각 카테고리에 해당하는 책의 개수를 반환")
    @GetMapping("/read/count")
    public ResponseEntity<BookCountResponse> readBookByCategory(){
        BookCountResponse bookCountResponse = bookService.countBooks();
        System.out.println("bookCountResponse = " + bookCountResponse);
        return new ResponseEntity<>(bookCountResponse, HttpStatus.OK);
    }


    @Operation(summary = "랜덤한 3개의 책을 반환", description = "랜덤한 3개의 책을 반환")
    @GetMapping("/read/random")
    public ResponseEntity<BookPreViewResponseList> readRandomBooks(){
        List<JSONObject> books = bookService.readRandomBooks();
        BookPreViewResponseList bookPreViewResponseList = new BookPreViewResponseList(books);
        return new ResponseEntity<>(bookPreViewResponseList, HttpStatus.OK);
    }

    @Operation(summary = "해당책의 doc_id를 입력시 그에 해당하는 책의 상세정보 반환",
            description = "해당책의 doc_id를 입력시 그에 해당하는 책의 상세정보 반환")
    @GetMapping("/read/detail/{doc_id}")
    public ResponseEntity<BookDetailResponse> readBookDetailByDocId(@PathVariable("doc_id") @Valid String docId){
        List<JSONObject> books = bookService.readBookDetailByDocId(docId);
        BookDetailResponse bookDetailResponse = new BookDetailResponse(books);
        return new ResponseEntity<>(bookDetailResponse, HttpStatus.OK);
    }

    @Operation(summary = "출판사이름으로 책의 preview를 가져옴", description = "파라미터로 출판사 입력시 해당하는 preview를 20개만 가져옴")
    @GetMapping("/read/publisher/{publisherName}")
    public ResponseEntity<BookPreViewResponseList> readBookByCategory(@PathVariable("publisherName") @Valid String publisher){
        List<JSONObject> jsonObjects = bookService.getBooksListByPublisher(publisher);
        BookPreViewResponseList bookPreViewResponseList = new BookPreViewResponseList(jsonObjects);
        return new ResponseEntity<>(bookPreViewResponseList, HttpStatus.OK);
    }

    @Operation(summary = "카테고리와 책의 이름으로 검색",
            description = "파라미터로 카테고리와 책의 이름을 입력시 해당하는 preview를 20개만 가져옴")
    @GetMapping("/read/bookNameSearch")
    public ResponseEntity<BookPreViewResponseList> readBookByBookNameAndCategory(
            @RequestParam(value = "bookName") String bookName, @RequestParam(value = "category") BookTag category){
        List<JSONObject> jsonObjects = bookService.getBooksListByBookNameAndTag(bookName,category);
        BookPreViewResponseList bookPreViewResponseList = new BookPreViewResponseList(jsonObjects);
        return new ResponseEntity<>(bookPreViewResponseList, HttpStatus.OK);
    }


}
