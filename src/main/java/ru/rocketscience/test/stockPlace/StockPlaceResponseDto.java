package ru.rocketscience.test.stockPlace;

import lombok.*;
import ru.rocketscience.test.stock.StockResponseDto;

import java.util.List;


@Value
public class StockPlaceResponseDto {

    Long id;
    String row;
    int shelf;
    int capacity;
    //тут лежит дто склада чтобы можно было взять её поля
    public StockResponseDto stock;
    //в ответе получаем номер stockPlace: первой добавленной
    int firstAddedStockPlaceNum;

    public List<StockPlaceResponseDto> stockPlaceList;
}
