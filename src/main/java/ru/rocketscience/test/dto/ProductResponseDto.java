package ru.rocketscience.test.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProductResponseDto {

    String name;
    BigDecimal price;  //нужно для того, чтобы были null значения, вместо 0
}
