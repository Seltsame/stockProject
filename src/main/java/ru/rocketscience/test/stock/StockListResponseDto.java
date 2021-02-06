package ru.rocketscience.test.stock;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
public class StockListResponseDto {

    List<String> stockList;
}
