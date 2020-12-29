package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockPlaceListResponseDto {

    //в ответе получаем номер stockPlace: первой добавленной
    int firstAddedStockPlaceNum;
}
