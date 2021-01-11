package ru.rosketscience.test.product;


import lombok.*;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.math.BigDecimal;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    String name;
    BigDecimal price;
    int stockPlaceId;

   public StockPlaceResponseDto stockPlaceResponseDto;
}
