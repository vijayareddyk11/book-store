package com.sporty.book.store.mappers;

import com.sporty.book.store.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.LoyaltyPointsDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    PurchaseMapper INSTANCE = Mappers.getMapper(PurchaseMapper.class);

    @Mapping(target = "canRedeemFreeBook", expression = "java(customer.getLoyaltyPoints() >= 10)")
    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "loyaltyPoints", target = "points")
    LoyaltyPointsDTO toLoyaltyPointsDto(Customer customer);

}
