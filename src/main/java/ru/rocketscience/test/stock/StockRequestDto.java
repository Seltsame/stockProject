package ru.rocketscience.test.stock;

import lombok.*;

@Value
public class StockRequestDto {

    long stockId;
    String name;
    String city;
}
