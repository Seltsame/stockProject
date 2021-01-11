package ru.rosketscience.test.stock;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;
@Data
@Builder
@Value
public class StockListStockPlaceDto {

    List<StockPlaceResponseDto> stockPlaceList;
}
