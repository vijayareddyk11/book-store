package com.sporty.book.store.repositories;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.enums.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByType(BookType type);
}

