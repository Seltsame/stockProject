package ru.rocketscience.test.dto.request;


import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductRequestDto {

    String name;
    BigDecimal price;
}
