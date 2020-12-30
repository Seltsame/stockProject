package ru.rocketscience.test.distributor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DistributorPlCapResponseDto {

    long stockPlaceId;
    String rowName; //название ряда, если не существует, надо создать
    int shelfNumber; //число полок
    int shelfCapacity;
}
