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

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

     /* Также getById() можно через Optional Лезем в репозиторий, чтобы достать сущность:
       Optional<Stock> entityToGet = stockRepository.findById(id);
        if (!entityToGet.isPresent()) { //проверяем на существование
            throw new ValidateException("Склада с id = " + id + " не существует");
        } //возвращаем DTO сущности
        return stockMapper.fromEntity(entityToGet.get());
        */

    public StockResponseDto getById(Long id) {
        Stock stockById = stockRepository.findById(id).orElseThrow(()
                -> new ValidateException("Склада с id = " + id + " не существует!"));
        return stockMapper.fromEntity(stockById);
    }

    //возвращаем ID после записи в репозиторий(если это нужно, если нет - void)
    public Long add(StockRequestDto stockRequestDto) {
        Stock addStock = stockRepository.save(stockMapper.toEntity(stockRequestDto));
        return addStock.getId();
    }


    @Transactional
    public void delete(Long id) {
        try {
            stockRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) { //При отсутствии в бд id удаляемой сущности, будет выброшена следующая ошибка:
            throw new ValidateException("Склада с id = " + id + " не существует!");
        }
    }

    @Transactional
    public void update(Long id, StockRequestDto stockRequestDto) {
        //через orElseTrow() напрямую тащим сущность из бд, если ее нет - пробрасываем кастомную ошибку
        Stock stockToUpdate = stockRepository.findById(id).orElseThrow(()
                -> new ValidateException("Склада с id = " + id + " не существует!"));
        //пишем напрямую в сущность новые пришедшие данные
        stockToUpdate.setName(stockRequestDto.getName());
        stockToUpdate.setCity(stockRequestDto.getCity());
        stockRepository.save(stockToUpdate); //
    }
}
// Также можно и через Optional: Лезем в репозиторий, чтобы достать сущность
/*        Optional<Stock> entityToUpdate = stockRepository.findById(id);
        if (!entityToUpdate.isPresent()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        //пишем напрямую в сущность новые пришедшие данные
        entityToUpdate.get().setCity(stockRequestDto.getCity());
        entityToUpdate.get().setName(stockRequestDto.getName());
        stockRepository.save(entityToUpdate.get()); //entityToUpdate.get() - как раз Entity. Сохраняем в бд*/

