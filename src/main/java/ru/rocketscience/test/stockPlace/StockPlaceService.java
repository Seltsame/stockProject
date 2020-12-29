package ru.rocketscience.test.stockPlace;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.stock.Stock;
import ru.rocketscience.test.stock.StockRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class StockPlaceService {

    private final StockPlaceRepository stockPlaceRepository;
    private final StockRepository stockRepository;
    private final StockPlaceMapper stockPlaceMapper;

    StockPlaceResponseDto getById(Long id) {
        StockPlace stockPlaceById = stockPlaceRepository.findById(id).orElseThrow(()
                -> new ValidateException("Места с id = " + id + " не существует!"));
        return stockPlaceMapper.fromEntity(stockPlaceById);
    }

    @Transactional
    Long add(StockPlaceRequestDto stockPlaceRequestDto) {
        StockPlace entity = stockPlaceMapper.toEntity(stockPlaceRequestDto);
        //лезем в репозиторий Stock'ов, чтобы достать id и добавить его в таблицу с
        Stock stock = stockRepository.getById(stockPlaceRequestDto.getStockId()).orElseThrow(()
                -> new ValidateException("Места с таким id не существует!"));
        //присваиваем stock в нашу сущность
        entity.setStock(stock);
        StockPlace addStockPlace
                = stockPlaceRepository.save(entity);
        return stockPlaceMapper.fromEntity(addStockPlace).getId();
    }

    @Transactional
    int addStockPlaces(StockPlaceListRequestDto stockPlaceListRequestDto) {
        int maxShelfNumber = stockPlaceRepository.getMaxShelfNumber();
        int firstAddedShelfNum = 0;
        StockPlace.StockPlaceBuilder builder = StockPlace.builder()
                .row(stockPlaceListRequestDto.getRowName())
                .capacity(stockPlaceListRequestDto.getShelfCapacity())
                .shelf(stockPlaceListRequestDto.getShelfNumber());

        for (int i = 1; i <= stockPlaceListRequestDto.getShelfNumber(); i++) {
            StockPlace entityToSave = builder.build();
            Stock stock
                    = stockRepository.getById(stockPlaceListRequestDto.getStock_id()).orElseThrow(()
                    -> new ValidateException("Места с таким id не существует!"));

            entityToSave.setStock(stock);
            entityToSave.setShelf(maxShelfNumber + i);
            stockPlaceRepository.save(entityToSave);
        }
        return firstAddedShelfNum + 1;
    }

    @Transactional
    void deleteById(Long id) {
        try {
            stockPlaceRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidateException("Места с id = " + id + " не существует!");
        }
    }

    @Transactional
    void updateById(Long id, StockPlaceRequestDto stockPlaceRequestDto) {
        StockPlace stockPlaceToUpdate = stockPlaceRepository.findById(id).orElseThrow(()
                -> new ValidateException("Места с id = " + id + " не существует!"));
        stockPlaceToUpdate.setRow(stockPlaceRequestDto.getRow());
        stockPlaceToUpdate.setShelf(stockPlaceRequestDto.getShelf());
        stockPlaceToUpdate.setCapacity(stockPlaceRequestDto.getCapacity());
        stockPlaceRepository.save(stockPlaceToUpdate);
    }
}
