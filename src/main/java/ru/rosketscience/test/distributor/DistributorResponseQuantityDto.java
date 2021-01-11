package ru.rosketscience.test.distributor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DistributorResponseQuantityDto {

    Long id;
    int productQuantity;
}
