package ru.rocketscience.test.product;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductFilterResponseDto {

    public List<ProductResponseDto> productList;
}
