package ru.rocketscience.test.product;

import org.mapstruct.Mapper;

@Mapper //см. натсройки Gradle
public interface ProductMapper {

    ProductResponseDto fromEntity(Product product);

    Product toEntity(ProductRequestDto productRequestDto);

    Product toEntityWithId(Long id, ProductRequestDto productRequestDto);
}
