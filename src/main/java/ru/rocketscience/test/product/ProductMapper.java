package ru.rocketscience.test.product;

import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    ProductResponseDto fromEntity(Product product);

    Product toEntity(ProductRequestDto productRequestDto);
}
