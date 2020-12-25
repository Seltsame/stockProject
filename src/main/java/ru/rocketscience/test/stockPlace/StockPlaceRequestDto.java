package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class StockPlaceRequestDto {

    String row;
    int rack;
    int capacity;
    //id склада для добавления в таблицу
    long stockId;
}
