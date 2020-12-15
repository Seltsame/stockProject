package ru.rocketscience.test.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockRequestDto {

    String name;
    String city;
}
