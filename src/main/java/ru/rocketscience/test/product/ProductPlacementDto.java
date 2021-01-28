package ru.rocketscience.test.product;

import lombok.Value;

@Value
public class ProductPlacementDto {

    long productId;
    long stockPlaceId;
    long quantityProduct; //добавление n-количества товара

}
