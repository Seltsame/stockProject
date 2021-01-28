package ru.rocketscience.test.stockPlace;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ManyStockPlacesResponseDto {

    int maxShelfNumber;
}
