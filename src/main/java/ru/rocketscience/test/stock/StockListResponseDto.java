package ru.rocketscience.test.stock;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StockListResponseDto {

    List<String> stockList;
}
