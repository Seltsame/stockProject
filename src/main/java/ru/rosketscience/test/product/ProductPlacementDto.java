package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPlacementDto {

    long productId;
    long stockPlaceId;
    long quantityProduct; //добавление n-количества товара

}
