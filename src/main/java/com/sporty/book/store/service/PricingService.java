package com.sporty.book.store.service;

import com.sporty.book.store.entities.Book;
import org.springframework.stereotype.Service;

@Service
public class PricingService {
    private static final int BUNDLE_SIZE = 3;
    private static final double REGULAR_BUNDLE_DISCOUNT = 0.10;
    private static final double OLD_EDITION_DISCOUNT = 0.20;
    private static final double OLD_EDITION_BUNDLE_DISCOUNT = 0.25;

    public double calculatePrice(Book book, int quantity, boolean isPartOfBundle) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        double basePrice = book.getBasePrice();
        double finalPrice = basePrice;

        switch (book.getType()) {
            case NEW_RELEASE:
                finalPrice = basePrice;
                break;

            case REGULAR:
                if (isPartOfBundle && quantity >= BUNDLE_SIZE) {
                    finalPrice = basePrice * (1 - REGULAR_BUNDLE_DISCOUNT);
                }
                break;

            case OLD_EDITION:
                finalPrice = basePrice * (1 - OLD_EDITION_DISCOUNT);
                if (isPartOfBundle && quantity >= BUNDLE_SIZE) {
                    finalPrice = basePrice * (1 - OLD_EDITION_BUNDLE_DISCOUNT);
                }
                break;

            default:
                throw new IllegalStateException("Unknown book type: " + book.getType());
        }

        return finalPrice;
    }
}
