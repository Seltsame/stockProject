package ru.rosketscience.test.distributor;

import lombok.Builder;
import lombok.Value;
import ru.rosketscience.test.stock.Stock;
import ru.rosketscience.test.stockPlace.StockPlace;

import java.util.List;


public class DistributorResponseDto {

    List<Stock> stockList;
    List<StockPlace> stockPlaceList;
    int capacity;
    Long id;


}
