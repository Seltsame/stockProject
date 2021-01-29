package ru.rocketscience.test.product;

import lombok.Value;

@Value
public class ProductMovementRequestDto {

    long productId;
    long stockPlaceIdFrom;
    long productQuantityToMove;
    long finalStockPlaceId;
}