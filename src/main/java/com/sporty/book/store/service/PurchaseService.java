package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.entities.Customer;
import com.sporty.book.store.entities.Purchase;
import com.sporty.book.store.entities.PurchaseItem;
import com.sporty.book.store.mappers.PurchaseMapper;
import com.sporty.book.store.repositories.BookRepository;
import com.sporty.book.store.repositories.CustomerRepository;
import com.sporty.book.store.repositories.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.PurchaseRequest;
import org.openapitools.model.PurchaseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final BookService bookService;
    private final PricingService pricingService;
    private final CustomerService customerService;
    private final PurchaseMapper purchaseMapper;

    public PurchaseService( PurchaseRepository purchaseRepository,
                            BookRepository bookRepository,
                            CustomerRepository customerRepository,
                            BookService bookService,
                            PricingService pricingService,
                            CustomerService customerService,
                            PurchaseMapper purchaseMapper){
        this.purchaseRepository = purchaseRepository;
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.bookService = bookService;
        this.pricingService = pricingService;
        this.customerService = customerService;
        this.purchaseMapper = purchaseMapper;

    }

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Map<UUID, Integer> quantities = request.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getBookId(),
                        item -> item.getQuantity()
                ));

        List<Book> books = bookRepository.findAllById(quantities.keySet());
        if (books.size() != quantities.size()) {
            throw new EntityNotFoundException("Some books were not found");
        }

        validateStock(books, quantities);

        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setUsedLoyaltyDiscount(request.getUseLoyaltyPoints());

        boolean isBundle = quantities.values().stream()
                .mapToInt(Integer::intValue)
                .sum() >= 3;

        List<PurchaseItem> items = new ArrayList<>();
        double subtotal = 0.0;

        for (Book book : books) {
            PurchaseItem item = createPurchaseItem(purchase, book, quantities.get(book.getId()), isBundle);
            items.add(item);
            subtotal += item.getPricePerUnit() * item.getQuantity();

            bookService.updateBookQuantity(book.getId(), item.getQuantity());
        }

        purchase.setItems(items);
        purchase.setSubtotal(subtotal);

        handleLoyaltyPoints(purchase, customer, items, request.getUseLoyaltyPoints());

        purchase = purchaseRepository.save(purchase);

        customerService.addLoyaltyPoints(customer.getId(), purchase.getLoyaltyPointsEarned());

        return purchaseMapper.toDto(purchase);
    }

    private void validateStock(List<Book> books, Map<UUID, Integer> quantities) {
        for (Book book : books) {
            int requestedQuantity = quantities.get(book.getId());
            if (book.getAvailableQuantity() < requestedQuantity) {
                throw new IllegalStateException(
                        String.format("Insufficient stock for book '%s'. Requested: %d, Available: %d",
                                book.getTitle(), requestedQuantity, book.getAvailableQuantity())
                );
            }
        }
    }

    private PurchaseItem createPurchaseItem(Purchase purchase, Book book, int quantity, boolean isBundle) {
        PurchaseItem item = new PurchaseItem();
        item.setPurchase(purchase);
        item.setBook(book);
        item.setQuantity(quantity);
        item.setPricePerUnit(pricingService.calculatePrice(book, quantity, isBundle));
        return item;
    }

    private void handleLoyaltyPoints(Purchase purchase, Customer customer, List<PurchaseItem> items, boolean useLoyaltyPoints) {
        double discount = 0.0;

        for (PurchaseItem item : items) {
            double originalPrice = item.getPricePerUnit();
            item.applyBookTypeDiscount();
            discount += (originalPrice - item.getPricePerUnit()) * item.getQuantity();
        }

        if (useLoyaltyPoints && customerService.canRedeemFreeBook(customer.getId())) {
            Optional<PurchaseItem> cheapestEligibleItem = items.stream()
                    .filter(PurchaseItem::isEligibleForLoyaltyDiscount)
                    .min(Comparator.comparing(PurchaseItem::getPricePerUnit));

            if (cheapestEligibleItem.isPresent()) {
                discount += cheapestEligibleItem.get().getPricePerUnit();
                customerService.useLoyaltyPoints(customer.getId());
                purchase.setLoyaltyPointsUsed(10);
            }
        }

        int pointsEarned = items.stream()
                .mapToInt(PurchaseItem::getQuantity)
                .sum();

        purchase.setDiscount(discount);
        purchase.setTotal(purchase.getSubtotal() - discount);
        purchase.setLoyaltyPointsEarned(pointsEarned);
    }

}
