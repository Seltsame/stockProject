package ru.rocketscience.test.stock;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);
}
