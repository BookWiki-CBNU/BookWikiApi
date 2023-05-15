package com.api.bigdata.api;

import com.api.bigdata.api.dto.BookPreViewResponse;
import com.api.bigdata.api.dto.BookPreViewResponseList;
import com.api.bigdata.api.dto.BookReadResponse;
import com.api.bigdata.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        List<JSONObject> jsonObjects = bookService.readBooksByTag(category);
        BookPreViewResponseList bookPreViewResponseList = new BookPreViewResponseList(jsonObjects);
        return new ResponseEntity<>(bookPreViewResponseList, HttpStatus.OK);
    }



}
