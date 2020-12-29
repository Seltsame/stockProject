package ru.rocketscience.test.stockPlace;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockPlaceMapper {

    StockPlaceResponseDto fromEntity(StockPlace stockPlace);

    StockPlace toEntity(StockPlaceRequestDto stockPlaceRequestDto);

    StockPlaceListResponseDto fromEntityPlaceId(StockPlace stockPlace);

    StockPlace toEntityPlaceData(StockPlaceListRequestDto stockPlaceListRequestDto);
}
