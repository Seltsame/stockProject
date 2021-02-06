package ru.rocketscience.test.product;

import lombok.Value;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.math.BigDecimal;

@Value
public class ProductRequestDto {

    String name;
    BigDecimal price;
    long stockPlaceId;

    StockPlaceResponseDto stockPlaceResponseDto;
}
