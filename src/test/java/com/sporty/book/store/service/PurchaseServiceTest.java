package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.entities.Customer;
import com.sporty.book.store.enums.BookType;
import com.sporty.book.store.mappers.PurchaseMapper;
import com.sporty.book.store.repositories.BookRepository;
import com.sporty.book.store.repositories.CustomerRepository;
import com.sporty.book.store.repositories.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PurchaseRequest;
import org.openapitools.model.PurchaseRequestItemsInner;
import org.openapitools.model.PurchaseResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private BookService bookService;
    @Mock
    private CustomerService customerService;
    @Mock
    private PurchaseMapper purchaseMapper;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void createPurchase_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setItems(null);
        request.setUseLoyaltyPoints(false);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> purchaseService.createPurchase(request)
        );

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(bookRepository, bookService, purchaseRepository, customerService);
    }

    @Test
    void createPurchase_ShouldThrowException_WhenSomeBooksNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);

        List<PurchaseRequestItemsInner> purchaseItems = new ArrayList<>();

        PurchaseRequestItemsInner item1 = new PurchaseRequestItemsInner();
        item1.setBookId(bookId1);
        item1.setQuantity(2);

        PurchaseRequestItemsInner item2 = new PurchaseRequestItemsInner();
        item2.setBookId(bookId2);
        item2.setQuantity(1);

        purchaseItems.add(item1);
        purchaseItems.add(item2);

        PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId);
        request.setItems(purchaseItems);
        request.setUseLoyaltyPoints(false);

        Book book2 = new Book();
        book2.setTitle("Book 1");
        book2.setBasePrice(100.0);
        book2.setType(BookType.REGULAR);
        book2.setBasePrice(10.00);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(bookRepository.findAllById(Set.of(bookId1, bookId2))).thenReturn(List.of(book2));


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> purchaseService.createPurchase(request)
        );

        assertEquals("Some books were not found", exception.getMessage());
        verify(bookRepository).findAllById(Set.of(bookId1, bookId2));
    }
}