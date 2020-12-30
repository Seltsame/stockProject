package ru.rocketscience.test.stock;

import lombok.Value;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;

@Value
public class StockResponseDto {

    Long id;
    String name;
    String city;
    List<StockPlace> stockPlaceList;
    StockPlaceResponseDto stockPlaceResponseDto;
    }
