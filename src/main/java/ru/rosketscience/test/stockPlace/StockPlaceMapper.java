package ru.rosketscience.test.stockPlace;

import org.mapstruct.Mapper;

@Mapper
public interface StockPlaceMapper {

    StockPlaceResponseDto fromEntity(StockPlace stockPlace);

    StockPlace toEntity(StockPlaceRequestDto stockPlaceRequestDto);
    }
