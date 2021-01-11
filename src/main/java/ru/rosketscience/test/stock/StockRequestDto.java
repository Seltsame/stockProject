package ru.rosketscience.test.stock;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRequestDto {

    String name;
    String city;
}
