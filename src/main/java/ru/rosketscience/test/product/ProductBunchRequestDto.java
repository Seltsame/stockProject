package ru.rosketscience.test.product;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductBunchRequestDto {

    long stockPlaceId;
    String name;
    BigDecimal price;
    int quantityProduct;
}
