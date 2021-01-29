package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockPlaceFilterDto {

    Long id;
    String row;
    int shelf;
    long quantity;
}
