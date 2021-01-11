package ru.rosketscience.test.stockPlace;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPlaceRequestDto {

    String row;
    int shelf;
    int capacity;
    //id склада для добавления в таблицу
    long stockId;



}
