package com.example.polarcatalogservice.controller;

import com.example.polarcatalogservice.dto.BookRequest;
import com.example.polarcatalogservice.dto.BookResponse;
import com.example.polarcatalogservice.exception.BookAlreadyInCatalogException;
import com.example.polarcatalogservice.exception.BookNotFoundException;
import com.example.polarcatalogservice.model.Book;
import com.example.polarcatalogservice.service.BookServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    BookServiceImpl bookService;

    @GetMapping("/books")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BookResponse>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse addBookToCatalog(@RequestBody @Valid BookRequest bookRequest) throws BookAlreadyInCatalogException {
        return bookService.addNewBook(bookRequest);
    }

    @PutMapping("/books/{id}")
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookResponse> updateBookInCatalog(@RequestBody @Valid BookRequest bookRequest,@PathVariable String id) {
        return bookService.updateBook(bookRequest,id);
    }

    @GetMapping("/")
    public String getGreeting(){
        return "Welcome to the Book Catalog";
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> findBookByISBN(@PathVariable String id) throws BookNotFoundException {
        return ResponseEntity.ok(bookService.findBookByISBN(id));
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteBookByISBN(@PathVariable String id) {
        bookService.deleteBookByISBN(id);
    }

}
