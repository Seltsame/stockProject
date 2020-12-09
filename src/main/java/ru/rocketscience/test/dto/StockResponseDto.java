package ru.rocketscience.test.dto;

import lombok.*;

@Value
@Builder
public class StockResponseDto {

    String name;
    String city;
}
