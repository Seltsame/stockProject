package ru.rocketscience.test.common;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdNameDto {
/*
DTO в общее, тк будет использоваться в поиске и пр, Stock, Product.*/
    Long id;
    String name;
}
