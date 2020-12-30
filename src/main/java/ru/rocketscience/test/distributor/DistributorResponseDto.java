package ru.rocketscience.test.distributor;

import lombok.Builder;
import lombok.Value;
import ru.rocketscience.test.stock.Stock;
import ru.rocketscience.test.stockPlace.StockPlace;

import java.util.List;

@Value
@Builder
public class DistributorResponseDto {

    List<Stock> stockList;
    List<StockPlace> stockPlaceList;
    int capacity;
    }
