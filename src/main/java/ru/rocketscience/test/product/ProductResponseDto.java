package ru.rocketscience.test.product;

import lombok.Value;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.math.BigDecimal;

@Value
public class ProductResponseDto {

    Long id; //id чтобы потом его использовать
    String name;
    BigDecimal price;  //нужно для того, чтобы были null значения, вместо 0
    StockPlaceResponseDto stockPlaceDto;
}
