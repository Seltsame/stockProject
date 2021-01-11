package ru.rosketscience.test.stock;

import lombok.*;
import ru.rosketscience.test.stockPlace.StockPlace;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDto {

    Long id;
    String name;
    String city;

     List<String> stockList;

     List<StockPlace> stockPlaceList;
     StockPlaceResponseDto stockPlaceResponseDto;
}
