package ru.rocketscience.test.product;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface ProductMapper {

    ProductResponseDto fromEntity(Product product);

    Product toEntity(ProductRequestDto productRequestDto);
}
