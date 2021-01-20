package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCriteriaFilterResponseDto {

    long productId;
    String productName;
    long stockId;
    long quantityProduct;
}
