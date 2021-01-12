package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    String name;
    BigDecimal price;
    long stockPlaceId;

    public StockPlaceResponseDto stockPlaceResponseDto;
}
