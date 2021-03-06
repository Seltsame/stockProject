package ru.rocketscience.test.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class StockResponseDto {

    Long id;
    String name;
    String city;

    StockPlaceResponseDto stockPlaceResponseDto;

    List<StockPlaceResponseDto> stockPlaceList;
}
