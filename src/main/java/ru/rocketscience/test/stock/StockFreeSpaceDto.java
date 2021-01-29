package ru.rocketscience.test.stock;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockFreeSpaceDto {

    Long id;
    Long freeSpace;
    int shelf;
    String row;
}
