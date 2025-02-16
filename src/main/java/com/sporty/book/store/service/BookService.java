package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.enums.BookType;
import com.sporty.book.store.mappers.BookMapper;
import com.sporty.book.store.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.BookDTO;
import org.openapitools.model.CreateBookRequest;
import org.openapitools.model.UpdateBookRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper){
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDTO> getAllBooks(BookType type) {
        List<Book> books;
        if (type == null) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository.findByType(type);
        }
        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO createBook(CreateBookRequest request) {
        Book book = bookMapper.toBook(request);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    public BookDTO updateBook(UUID id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        bookMapper.updateEntity(book, request);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    public void deleteBook(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public void updateBookQuantity(UUID bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        int newQuantity = book.getAvailableQuantity() - quantity;
        if (newQuantity < 0) {
            throw new IllegalStateException("Insufficient stock for book: " + book.getTitle());
        }

        book.setAvailableQuantity(newQuantity);
        bookRepository.save(book);
    }


}
