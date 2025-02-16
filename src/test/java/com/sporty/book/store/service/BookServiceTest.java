package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.enums.BookType;
import com.sporty.book.store.mappers.BookMapper;
import com.sporty.book.store.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BookDTO;
import org.openapitools.model.CreateBookRequest;
import org.openapitools.model.UpdateBookRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void addBook_ShouldSaveAndReturnBook() {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Sample Book");
        request.setAuthor("John Doe");
        request.setIsbn("1234567890");
        request.setType(CreateBookRequest.TypeEnum.REGULAR);
        request.setBasePrice(29.99);
        request.setAvailableQuantity(10);

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setType(BookType.REGULAR);
        book.setBasePrice(request.getBasePrice());
        book.setAvailableQuantity(request.getAvailableQuantity());

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setType(BookDTO.TypeEnum.REGULAR);
        bookDTO.setBasePrice(book.getBasePrice());
        bookDTO.setAvailableQuantity(book.getAvailableQuantity());

        when(bookMapper.toBook(request)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        BookDTO result = bookService.createBook(request);

        assertNotNull(result);

        verify(bookMapper, times(1)).toBook(request);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
    }


    @Test
    void updateBook_ShouldUpdateAndReturnUpdatedBook() {
        UUID bookId = UUID.randomUUID();

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");
        existingBook.setIsbn("1234567890");
        existingBook.setType(BookType.REGULAR);
        existingBook.setBasePrice(19.99);
        existingBook.setAvailableQuantity(5);

        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("New Title");
        request.setAuthor("New Author");
        request.setIsbn("0987654321");
        request.setType(UpdateBookRequest.TypeEnum.NEW_RELEASE);
        request.setBasePrice(29.99);
        request.setAvailableQuantity(10);

        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle(request.getTitle());
        updatedBook.setAuthor(request.getAuthor());
        updatedBook.setIsbn(request.getIsbn());
        updatedBook.setType(BookType.NEW_RELEASE);
        updatedBook.setBasePrice(request.getBasePrice());
        updatedBook.setAvailableQuantity(request.getAvailableQuantity());

        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setId(bookId);
        updatedBookDTO.setTitle(request.getTitle());
        updatedBookDTO.setAuthor(request.getAuthor());
        updatedBookDTO.setIsbn(request.getIsbn());
        updatedBookDTO.setType(BookDTO.TypeEnum.NEW_RELEASE);
        updatedBookDTO.setBasePrice(request.getBasePrice());
        updatedBookDTO.setAvailableQuantity(request.getAvailableQuantity());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        doAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            UpdateBookRequest updateRequest = invocation.getArgument(1);
            book.setTitle(updateRequest.getTitle());
            book.setAuthor(updateRequest.getAuthor());
            book.setIsbn(updateRequest.getIsbn());
            book.setType(BookType.valueOf(updateRequest.getType().name()));
            book.setBasePrice(updateRequest.getBasePrice());
            book.setAvailableQuantity(updateRequest.getAvailableQuantity());
            return null;
        }).when(bookMapper).updateEntity(existingBook, request);
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDTO);

        BookDTO result = bookService.updateBook(bookId, request);

        System.out.println("Updated BookDTO: " + result);

        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getAuthor(), result.getAuthor());
        assertEquals(request.getIsbn(), result.getIsbn());
        assertEquals(BookDTO.TypeEnum.valueOf(request.getType().name()), result.getType());
        assertEquals(request.getBasePrice(), result.getBasePrice());
        assertEquals(request.getAvailableQuantity(), result.getAvailableQuantity());

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).updateEntity(existingBook, request);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper, times(1)).toDto(updatedBook);
    }

    @Test
    void deleteBook_ShouldDeleteBookIfExists() {
        UUID bookId = UUID.randomUUID();


        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);

        bookService.deleteBook(bookId);

        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_ShouldThrowExceptionIfBookNotFound() {
        UUID bookId = UUID.randomUUID();

        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(bookId));

        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void updateBookQuantity_ShouldUpdateQuantityIfSufficientStock() {
        UUID bookId = UUID.randomUUID();
        int initialQuantity = 10;
        int quantityToSubtract = 3;
        int expectedNewQuantity = initialQuantity - quantityToSubtract;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Sample Book");
        book.setAvailableQuantity(initialQuantity);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookService.updateBookQuantity(bookId, quantityToSubtract);

        assertEquals(expectedNewQuantity, book.getAvailableQuantity());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBookQuantity_ShouldThrowExceptionIfBookNotFound() {
        UUID bookId = UUID.randomUUID();
        int quantityToSubtract = 3;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.updateBookQuantity(bookId, quantityToSubtract));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBookQuantity_ShouldThrowExceptionIfInsufficientStock() {
        UUID bookId = UUID.randomUUID();
        int initialQuantity = 5;
        int quantityToSubtract = 10;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Sample Book");
        book.setAvailableQuantity(initialQuantity);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(IllegalStateException.class, () -> bookService.updateBookQuantity(bookId, quantityToSubtract));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any());
    }

}