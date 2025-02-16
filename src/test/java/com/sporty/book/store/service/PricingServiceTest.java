package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.enums.BookType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    @Test
    void calculatePrice_ShouldReturnBasePrice_ForNewRelease() {

        Book book = new Book();
        book.setTitle("Test Book");
        book.setBasePrice(100.0);
        book.setType(BookType.NEW_RELEASE);

        double price = pricingService.calculatePrice(book, 1, false);

        assertEquals(100.0, price);
    }

    @Test
    void calculatePrice_ShouldReturnBasePrice_ForRegularBookWithoutBundle() {

        Book book = new Book();
        book.setTitle("Regular Book");
        book.setBasePrice(100.0);
        book.setType(BookType.REGULAR);

        double price = pricingService.calculatePrice(book, 2, false);

        assertEquals(100.0, price);
    }

    @Test
    void calculatePrice_ShouldApplyRegularBundleDiscount_WhenQuantityMeetsThreshold() {

        Book book = new Book();
        book.setTitle("Regular Book");
        book.setBasePrice(100.0);
        book.setType(BookType.REGULAR);

        double price = pricingService.calculatePrice(book, 3, true);

        assertEquals(90.0, price);
    }

    @Test
    void calculatePrice_ShouldApplyOldEditionDiscount_WhenNotInBundle() {

        Book book = new Book();
        book.setTitle("Old Edition Book");
        book.setBasePrice(100.0);
        book.setType(BookType.OLD_EDITION);

        double price = pricingService.calculatePrice(book, 1, false);

        assertEquals(80.0, price);
    }

    @Test
    void calculatePrice_ShouldApplyOldEditionBundleDiscount_WhenQuantityMeetsThreshold() {

        Book book = new Book();
        book.setTitle("Old Edition Book");
        book.setBasePrice(100.0);
        book.setType(BookType.OLD_EDITION);

        double price = pricingService.calculatePrice(book, 3, true);

        assertEquals(75.0, price);
    }

    @Test
    void calculatePrice_ShouldThrowException_WhenBookIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pricingService.calculatePrice(null, 1, false)
        );

        assertEquals("Book cannot be null", exception.getMessage());
    }

}