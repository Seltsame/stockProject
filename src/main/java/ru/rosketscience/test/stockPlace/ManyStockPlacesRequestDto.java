package ru.rosketscience.test.stockPlace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rosketscience.test.stock.StockResponseDto;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManyStockPlacesRequestDto {

    String row;
    int capacity;
    //тут лежит дто склада чтобы можно было взять её поля
    int stockPlaceQuantity;
    long stockId;
    public StockResponseDto stock;
}
