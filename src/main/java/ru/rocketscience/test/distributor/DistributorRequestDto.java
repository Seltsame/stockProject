package ru.rocketscience.test.distributor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DistributorRequestDto {

    String cityName;
    long id;
}
