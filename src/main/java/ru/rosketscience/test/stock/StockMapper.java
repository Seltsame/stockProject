package ru.rosketscience.test.stock;

import org.mapstruct.Mapper;

@Mapper
public interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);
}
