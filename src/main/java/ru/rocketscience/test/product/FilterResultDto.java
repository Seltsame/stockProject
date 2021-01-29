package ru.rocketscience.test.product;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.rocketscience.test.common.IdNameDto;
import ru.rocketscience.test.stockPlace.StockPlaceFilterDto;

import java.util.List;

@Value
@Builder
public class FilterResultDto {

    IdNameDto product;
    List<StockDto> stockDto;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class StockDto extends IdNameDto {
        long quantity;
        String city;
        List<StockPlaceFilterDto> stockPlaceFilterDto;//потом перейти на List<StockPlace> -?

        public StockDto(Long id, String name) {
            super(id, name);
        }
    }
}

