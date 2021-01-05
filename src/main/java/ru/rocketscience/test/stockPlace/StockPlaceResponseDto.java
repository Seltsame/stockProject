package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;
import ru.rocketscience.test.stock.StockResponseDto;

@Value
public class StockPlaceResponseDto {

    Long id;
    String row;
    int shelf;
    int capacity;
    //тут лежит дто склада чтобы можно было взять её поля
   public StockResponseDto stock;
    }
