package ru.rocketscience.test.product;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ProductMapper {

    ProductResponseDto fromEntity(Product product);

    Product toEntity(ProductRequestDto productRequestDto);
}
