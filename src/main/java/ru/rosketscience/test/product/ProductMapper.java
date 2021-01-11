package ru.rosketscience.test.product;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
public interface ProductMapper {

    ProductResponseDto fromEntity(Product product);

    Product toEntity(ProductRequestDto productRequestDto);
}
