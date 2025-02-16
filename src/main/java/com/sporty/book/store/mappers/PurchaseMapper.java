package com.sporty.book.store.mappers;

import com.sporty.book.store.entities.Purchase;
import com.sporty.book.store.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.PurchaseResponse;
import org.openapitools.model.PurchaseResponseItemsInner;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PurchaseMapper {
    PurchaseMapper INSTANCE = Mappers.getMapper(PurchaseMapper.class);

    @Mapping(target = "purchaseId", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "items", source = "items")
    PurchaseResponse toDto(Purchase purchase);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "quantity", source = "quantity")
    PurchaseResponseItemsInner toResponseItems(PurchaseItem item);

    List<PurchaseResponseItemsInner> toResponseItemsList(List<PurchaseItem> items);

}
