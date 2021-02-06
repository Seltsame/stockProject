package ru.rocketscience.test.product;

import lombok.Value;

import java.util.List;

@Value
public class ProductFilterResponseDto {

    List<ProductResponseDto> productList;
}
