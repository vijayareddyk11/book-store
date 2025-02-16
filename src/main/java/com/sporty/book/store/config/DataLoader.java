package com.sporty.book.store.config;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.entities.Customer;
import com.sporty.book.store.enums.BookType;
import com.sporty.book.store.repositories.BookRepository;
import com.sporty.book.store.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    public DataLoader(BookRepository bookRepository, CustomerRepository customerRepository) {
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {
        Book book1 = new Book();
        book1.setTitle("The New Earth");
        book1.setAuthor("John Smith");
        book1.setIsbn("1234567890");
        book1.setType(BookType.NEW_RELEASE);
        book1.setBasePrice(29.99);
        book1.setAvailableQuantity(100);

        Book book2 = new Book();
        book2.setTitle("Programming 101");
        book2.setAuthor("Jane Doe");
        book2.setIsbn("0987654321");
        book2.setType(BookType.REGULAR);
        book2.setBasePrice(19.99);
        book2.setAvailableQuantity(50);

        Book book3 = new Book();
        book3.setTitle("Classic Literature");
        book3.setAuthor("William Johnson");
        book3.setIsbn("5432109876");
        book3.setType(BookType.OLD_EDITION);
        book3.setBasePrice(9.99);
        book3.setAvailableQuantity(30);

        bookRepository.saveAll(Arrays.asList(book1, book2, book3));


        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setLoyaltyPoints(0);

        customerRepository.save(customer);
    }
}
