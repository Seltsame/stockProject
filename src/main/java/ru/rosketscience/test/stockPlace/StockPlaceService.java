package ru.rosketscience.test.stockPlace;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.stock.Stock;
import ru.rosketscience.test.stock.StockRepository;

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
    int addStockPlaces(ManyStockPlacesRequestDto manyStockPlacesRequestDto) {

        long stockId = manyStockPlacesRequestDto.getStockId();
        Stock stock = stockRepository.getById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует!"));
        /* если ряд существует, будет взят номер последней полочки, если нет, то вернется 1,
        поэтому, не нужна проверка на существование ряда, тк он в любом случае запишется, если
        существует, про продолжит с максимально дальней полочки, если нет, то с 1й */
        int maxShelfNumber = stockPlaceRepository.getMaxShelfNumber(manyStockPlacesRequestDto.getRow());
        StockPlace.StockPlaceBuilder builder = StockPlace.builder()
                .row(manyStockPlacesRequestDto.getRow())
                .stock(stock)  //присваиваем id необходимого склада
                .capacity(manyStockPlacesRequestDto.getCapacity());

        //создаём Entity, записываем ее в бд. (в цикле делается для того, чтобы записать пачку идекнтичных полочек)
        for (int i = 1; i <= manyStockPlacesRequestDto.stockPlaceQuantity; i++) {
            //создаем объект в цикле, чтобы Гибернейт гарантированно создал новую сущность
            StockPlace entityToSave = builder.build();
            //присваиваем порядковый номер полочки + 1, чтобы в бд полочки шли по порядку
            entityToSave.setShelf(maxShelfNumber + i);
            stockPlaceRepository.save(entityToSave);
        }
        //по тз надо вернуть номер первого добавленного места
        return maxShelfNumber + 1;
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
