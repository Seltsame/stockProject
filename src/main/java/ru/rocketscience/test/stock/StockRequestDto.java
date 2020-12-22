package ru.rocketscience.test.stock;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockRequestDto {

    String name;
    String city;
}
