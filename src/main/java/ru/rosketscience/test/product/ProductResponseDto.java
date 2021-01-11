package ru.rosketscience.test.product;

import lombok.*;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    Long id; //id чтобы потом его использовать
    String name;
    BigDecimal price;  //нужно для того, чтобы были null значения, вместо 0
    StockPlaceResponseDto stockPlaceDto;
    int quantityProduct;

}