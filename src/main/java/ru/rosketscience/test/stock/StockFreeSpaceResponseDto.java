package ru.rosketscience.test.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockFreeSpaceResponseDto {

    long stockFreeSpace;
}
