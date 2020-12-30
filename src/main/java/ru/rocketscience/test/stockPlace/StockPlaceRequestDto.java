package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockPlaceRequestDto {

    String row;
    int shelf;
    int capacity;
    //id склада для добавления в таблицу
    long stockId;



}
