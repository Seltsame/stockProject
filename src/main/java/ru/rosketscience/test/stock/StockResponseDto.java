package ru.rosketscience.test.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDto {

    Long id;
    String name;
    String city;

    StockPlaceResponseDto stockPlaceResponseDto;

    List<StockPlaceResponseDto> stockPlaceList;
}
