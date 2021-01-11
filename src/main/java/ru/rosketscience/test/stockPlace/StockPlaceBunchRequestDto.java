package ru.rosketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockPlaceBunchRequestDto {

    long stockId;
    String rowName; //название ряда, если не существует, надо создать
    int shelfNumber; //число полок
    int shelfCapacity; //вместимость полки


}
