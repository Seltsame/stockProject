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
<<<<<<< HEAD
    long stockId;
=======
    long stock_id;

>>>>>>> 07031b5 (добавлен метод addStockPlaces, который позволяет добавлять полки в неограниченном количестве + тесты)
}
