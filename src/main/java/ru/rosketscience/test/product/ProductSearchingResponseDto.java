package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchingResponseDto {

    long productId;
    long stockId;
    long quantityProduct;
}
