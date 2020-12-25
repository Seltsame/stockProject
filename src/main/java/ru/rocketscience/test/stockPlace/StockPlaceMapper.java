package ru.rocketscience.test.stockPlace;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockPlaceMapper {

    StockPlaceResponseDto fromEntity(StockPlace stockPlace);

    StockPlace toEntity(StockPlaceRequestDto stockPlaceRequestDto);
}
