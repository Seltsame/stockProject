package ru.rocketscience.test.stock;

import lombok.*;

import java.util.List;

@Value
@Builder
public class StockListResponseDto {

    List<String> stockList;
}
