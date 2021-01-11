package ru.rosketscience.test.stockPlace;

import lombok.*;
import ru.rosketscience.test.stock.StockResponseDto;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPlaceResponseDto {

    Long id;
    String row;
    int shelf;
    int capacity;
    //тут лежит дто склада чтобы можно было взять её поля
   public StockResponseDto stock;

 public   List<StockPlaceResponseDto> stockPlaceList;

}
