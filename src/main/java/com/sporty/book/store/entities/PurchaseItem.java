package com.sporty.book.store.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @ManyToOne
    private Purchase purchase;

    @ManyToOne
    private Book book;

    private Integer quantity;
    private Double pricePerUnit;

    public void applyBookTypeDiscount() {
        this.pricePerUnit = book.getType().applyDiscount(this.pricePerUnit, this.quantity);
    }

    public boolean isEligibleForLoyaltyDiscount() {
        return book.getType().isEligibleForLoyaltyDiscount();
    }
}
