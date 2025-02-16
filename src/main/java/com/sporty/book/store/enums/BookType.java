package com.sporty.book.store.enums;

public enum BookType {
    NEW_RELEASE {
        @Override
        public double applyDiscount(double price, int quantity) {
            return price;
        }
    },
    REGULAR {
        @Override
        public double applyDiscount(double price, int quantity) {
            return (quantity >= 3) ? price * 0.9 : price;
        }
    },
    OLD_EDITION {
        @Override
        public double applyDiscount(double price, int quantity) {
            return price * 0.8;
        }
    };

    public boolean isEligibleForLoyaltyDiscount() {
        return true;
    }

    public abstract double applyDiscount(double price, int quantity);
}
