package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.model.Stock;
import ru.rocketscience.test.repository.StockRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public StockResponseDto getById(Long id) {
        Optional<Stock> optStock = stockRepository.findById(id);
        if (!optStock.isPresent()) {
            return null;
        }
        return getStockResponseDtoFromStock(optStock.get());
    }

    public static StockResponseDto getStockResponseDtoFromStock(Stock stock) {
        return StockResponseDto.builder()
                .name(stock.getName())
                .city(stock.getCity())
                .build();
    }
}