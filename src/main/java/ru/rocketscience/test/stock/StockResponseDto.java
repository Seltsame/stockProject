package ru.rocketscience.test.stock;

import lombok.Value;

@Value
public class StockResponseDto {

    Long id;
    String name;
    String city;
}
