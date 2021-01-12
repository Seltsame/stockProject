package ru.rosketscience.test.stockPlace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPlaceRequestDto {

    long id;
    String row;
    int shelf;
    int capacity;

    //id склада для добавления в таблицу
    long stockId;



}
