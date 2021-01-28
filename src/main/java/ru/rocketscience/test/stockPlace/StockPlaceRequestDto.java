package ru.rocketscience.test.stockPlace;

import lombok.*;

@Value
public class StockPlaceRequestDto {

    long id;
    String row;
    int shelf;
    int capacity;

    //id склада для добавления в таблицу
    long stockId;



}
