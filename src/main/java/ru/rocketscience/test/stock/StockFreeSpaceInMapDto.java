package ru.rocketscience.test.stock;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class StockFreeSpaceInMapDto {

    Map<Long, Long> stockPlaceIdFreeSpaceByStockId;
}
