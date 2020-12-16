package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;
import ru.rocketscience.test.mapper.StockMapper;
import ru.rocketscience.test.model.Stock;
import ru.rocketscience.test.repository.StockRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public StockResponseDto getById(Long id) {
        Optional<Stock> optStock = stockRepository.findById(id);
        if (!optStock.isPresent()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        return stockMapper.fromEntity(optStock.get());
    }

    //возвращаем ID после записи в репозиторий(если это нужно, если нет - void)
    public Long addStock(StockRequestDto stockRequestDto) {
        Stock save = stockRepository.save(stockMapper.toEntity(stockRequestDto));
        return save.getId();
    }

    public void deleteStock(Long id) {
        Optional<Stock> byId = stockRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        stockRepository.deleteById(id);
    }
}
