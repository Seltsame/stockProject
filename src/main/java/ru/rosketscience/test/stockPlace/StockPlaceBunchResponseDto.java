package ru.rosketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockPlaceBunchResponseDto {

    //в ответе получаем номер stockPlace: первой добавленной
    int firstAddedStockPlaceNum;
}
