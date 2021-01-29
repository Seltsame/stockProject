package ru.rocketscience.test.stockPlace;

import org.mapstruct.Mapper;

@Mapper //см. настройки Gradle
public interface StockPlaceMapper {

    StockPlaceResponseDto fromEntity(StockPlace stockPlace);

    StockPlace toEntity(StockPlaceRequestDto stockPlaceRequestDto);
}
