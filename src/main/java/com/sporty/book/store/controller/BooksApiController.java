package com.sporty.book.store.controller;

import com.sporty.book.store.service.BookService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.openapitools.api.BooksApi;
import org.openapitools.model.BookDTO;
import org.openapitools.model.CreateBookRequest;
import org.openapitools.model.UpdateBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;

@RestController
public class BooksApiController implements BooksApi {

    @Autowired
    private BookService bookService;

    @Override
    public ResponseEntity<List<BookDTO>> getBooks(String type) {
        List<BookDTO> books = bookService.getAllBooks(null);
        return ResponseEntity.ok(books);
    }

    @Override
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody CreateBookRequest createBookRequest) {
        BookDTO createdBook = bookService.createBook(createBookRequest);
        return ResponseEntity.ok(createdBook);
    }

    @Override
    public ResponseEntity<BookDTO> updateBook( @PathVariable("id") UUID id, @Valid @RequestBody UpdateBookRequest updateBookRequest){
        BookDTO bookDTO = bookService.updateBook(id, updateBookRequest);
        return ResponseEntity.ok(bookDTO);
    }

    @Override
    public ResponseEntity<Void> deleteBook(UUID id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
