package ru.rocketscience.test.mapper;

import org.mapstruct.Mapper;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto fromEntity(Product product);
}
