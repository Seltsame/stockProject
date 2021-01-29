package ru.rocketscience.test.product;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductMovementResponseDto {

    long productId;
    long stockplaceId;
    long stockId;
    long quantityProduct;
}