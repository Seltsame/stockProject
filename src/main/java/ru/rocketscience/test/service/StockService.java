package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.StockResponseDto;
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
}
