package ru.rocketscience.test.stock;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    StockResponseDto fromEntity(Stock stock);

    Stock toEntity(StockRequestDto stockRequestDto);
}
