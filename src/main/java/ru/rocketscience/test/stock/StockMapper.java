package ru.rocketscience.test.stock;

import org.mapstruct.Mapper;

@Mapper //см. натсройки Gradle
public interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);

}
