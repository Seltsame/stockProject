package ru.rosketscience.test.stock;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
public interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);
}
