package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;
import ru.rocketscience.test.mapper.StockMapper;
import ru.rocketscience.test.model.Stock;
import ru.rocketscience.test.repository.StockRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public StockResponseDto getById(Long id) {
        Optional<Stock> entityToGet = stockRepository.findById(id);
        if (!entityToGet.isPresent()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        return stockMapper.fromEntity(entityToGet.get());
    }

    //возвращаем ID после записи в репозиторий(если это нужно, если нет - void)
    public Long addStock(StockRequestDto stockRequestDto) {
        Stock addStock = stockRepository.save(stockMapper.toEntity(stockRequestDto));
        return addStock.getId();
    }

    @Transactional
    public void deleteStock(Long id) {
        try {
            stockRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
    }

    @Transactional
    public void updateStock(Long id, StockRequestDto stockRequestDto) {
        Optional<Stock> entityToUpdate = stockRepository.findById(id);
        if (!entityToUpdate.isPresent()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        //пишем напрямую в сущность новые пришедшие данные
        entityToUpdate.get().setCity(stockRequestDto.getCity());
        entityToUpdate.get().setName(stockRequestDto.getName());
        stockRepository.save(entityToUpdate.get()); //entityToUpdate.get() - как раз Entity
    }
}
