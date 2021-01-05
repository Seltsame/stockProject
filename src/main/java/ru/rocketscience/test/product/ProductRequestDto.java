package ru.rocketscience.test.product;


import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductRequestDto {

    String name;
    BigDecimal price;
    int stockPlaceId;

}
