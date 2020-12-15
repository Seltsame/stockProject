package ru.rocketscience.test.mapper;

import org.mapstruct.Mapper;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;
import ru.rocketscience.test.model.Stock;

@Mapper(componentModel = "spring")
public interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);
}
