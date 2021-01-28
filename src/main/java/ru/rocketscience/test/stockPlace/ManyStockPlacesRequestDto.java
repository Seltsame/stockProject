package ru.rocketscience.test.stockPlace;

import lombok.*;
import ru.rocketscience.test.stock.StockResponseDto;


@Value
public class ManyStockPlacesRequestDto {

    String row;
    int capacity;
    //тут лежит дто склада чтобы можно было взять её поля
    int stockPlaceQuantity;
    long stockId;
    public StockResponseDto stock;
}
