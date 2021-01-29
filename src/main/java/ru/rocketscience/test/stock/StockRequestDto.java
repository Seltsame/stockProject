package ru.rocketscience.test.stock;

import lombok.Value;

@Value
public class StockRequestDto {

    long stockId;
    String name;
    String city;
}
