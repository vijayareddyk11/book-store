package com.sporty.book.store.controller;

import com.sporty.book.store.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BookDTO;
import org.openapitools.model.CreateBookRequest;
import org.openapitools.model.UpdateBookRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BooksApiControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BooksApiController booksApiController;

    private UUID bookId;
    private BookDTO bookDTO;
    private CreateBookRequest createBookRequest;
    private UpdateBookRequest updateBookRequest;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();

        bookDTO = new BookDTO();
        bookDTO.setId(bookId);
        bookDTO.setTitle("Book Title");
        bookDTO.setBasePrice(100.0);
        bookDTO.setType(BookDTO.TypeEnum.REGULAR);

        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("Book Title");
        createBookRequest.setBasePrice(100.0);
        createBookRequest.setType(CreateBookRequest.TypeEnum.REGULAR);

        updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setTitle("Updated Book Title");
        updateBookRequest.setBasePrice(120.0);
        updateBookRequest.setType(UpdateBookRequest.TypeEnum.OLD_EDITION);

    }


    @Test
    void testGetBooks() {
        when(bookService.getAllBooks(null)).thenReturn(List.of(bookDTO));

        ResponseEntity<List<BookDTO>> response = booksApiController.getBooks(null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(bookDTO.getTitle(), response.getBody().get(0).getTitle());
        verify(bookService, times(1)).getAllBooks(null);
    }

    @Test
    void testAddBook() {

        when(bookService.createBook(createBookRequest)).thenReturn(bookDTO);

        ResponseEntity<BookDTO> response = booksApiController.addBook(createBookRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(bookDTO.getTitle(), response.getBody().getTitle());
        verify(bookService, times(1)).createBook(createBookRequest);
    }

    @Test
    void testUpdateBook() {

        when(bookService.updateBook(bookId, updateBookRequest)).thenReturn(bookDTO);

        ResponseEntity<BookDTO> response = booksApiController.updateBook(bookId, updateBookRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(bookDTO.getTitle(), response.getBody().getTitle());
        verify(bookService, times(1)).updateBook(bookId, updateBookRequest);
    }

    @Test
    void testDeleteBook() {

        doNothing().when(bookService).deleteBook(bookId);

        ResponseEntity<Void> response = booksApiController.deleteBook(bookId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(bookService, times(1)).deleteBook(bookId);
    }
}
