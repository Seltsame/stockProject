package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;
import ru.rocketscience.test.stock.StockResponseDto;

import java.util.List;


@Value
@Builder
public class StockPlaceResponseDto {

    Long id;
    String row;
    int shelf;
    int capacity;
    long shelfNum;
    //номер полочки
    //тут лежит дто склада чтобы можно было взять её поля
    public StockResponseDto stock;
    //в ответе получаем номер stockPlace: первой добавленной
    int firstAddedStockPlaceNum; //уйдёт при refactor метода по добавлению полочек

    public List<StockPlaceResponseDto> stockPlaceList;
}
