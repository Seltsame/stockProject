package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMovementRequestDto {

    long productId;
    long stockPlaceIdFrom;
    long productQuantityToMove;
    long finalStockPlaceId;
}