package ru.rosketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//валидация через паттерны на sql инъекции, проверка строк
//обсудить с Кириллом
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDto {

    String namePart;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
