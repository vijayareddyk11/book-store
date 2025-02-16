package com.sporty.book.store.mappers;

import com.sporty.book.store.entities.Book;
import com.sporty.book.store.enums.BookType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.BookDTO;
import org.openapitools.model.CreateBookRequest;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "basePrice", source = "basePrice")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    BookDTO toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "basePrice", source = "basePrice")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    Book toBook(CreateBookRequest bookDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Book book, org.openapitools.model.UpdateBookRequest bookDTO);


    default BookDTO.TypeEnum mapType(BookType type) {
        if (type == null) {
            return null;
        }
        return BookDTO.TypeEnum.valueOf(type.name());
    }

    default BookType mapType(CreateBookRequest.TypeEnum type) {
        if (type == null) {
            return null;
        }
        return BookType.valueOf(type.name());
    }
}
