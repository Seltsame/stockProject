package ru.rosketscience.test.stock;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.Map;

@Data
@Builder
@Value
public class StockCapacityDto {

    Map<Long, Integer> freeSpaceByStockId;
}
