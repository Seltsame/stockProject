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
        return stockPlaceMapper.fromEntity(getStockPlaceEntityById(id));
    }

    @Transactional
    Long add(StockPlaceRequestDto stockPlaceRequestDto) {
        StockPlace stockPlaceEntity = stockPlaceMapper.toEntity(stockPlaceRequestDto);
        //лезем в репозиторий Stock'ов, чтобы достать id и добавить его в таблицу с
        Stock stock = getStockEntityById(stockPlaceRequestDto.getStockId());
        //присваиваем stock в нашу сущность
        stockPlaceEntity.setStock(stock);
        StockPlace addStockPlace
                = stockPlaceRepository.save(stockPlaceEntity);
        return stockPlaceMapper.fromEntity(addStockPlace).getId();
    }

    //добавление  n-количества полочек на склад в один и тот же ряд
    @Transactional
    ManyStockPlacesResponseDto addStockPlaces(ManyStockPlacesRequestDto manyStockPlacesRequestDto) {
        long stockId = manyStockPlacesRequestDto.getStockId();
        Stock stock = getStockEntityById(stockId);
        /* если ряд существует, будет взят номер последней полочки, если нет, то вернется 1,
        поэтому, не нужна проверка на существование ряда, тк он в любом случае запишется, если
        существует, про продолжит с максимально дальней полочки, если нет, то с 1й */
        int maxShelfNumber = stockPlaceRepository.getMaxShelfNumber(manyStockPlacesRequestDto.getRow());
        StockPlace.StockPlaceBuilder builder = StockPlace.builder()
                .row(manyStockPlacesRequestDto.getRow())
                .stock(stock)  //присваиваем id необходимого склада
                .capacity(manyStockPlacesRequestDto.getCapacity());

        //создаём Entity, записываем ее в бд. (в цикле делается для того, чтобы записать пачку идекнтичных полочек)
        for (int i = 1; i <= manyStockPlacesRequestDto.getStockPlaceQuantity(); i++) {
            //создаем объект в цикле, чтобы Гибернейт гарантированно создал новую сущность
            StockPlace entityToSave = builder.build();
            //присваиваем порядковый номер полочки + 1, чтобы в бд полочки шли по порядку
            entityToSave.setShelf(maxShelfNumber + i);
            stockPlaceRepository.save(entityToSave);
        }
        //по тз надо вернуть номер первого добавленного места
        return ManyStockPlacesResponseDto.builder()
                .maxShelfNumber(maxShelfNumber + 1)
                .build();
    }

    @Transactional
    void delete(Long id) {
        try {
            stockPlaceRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidateException("Места с id = " + id + " не существует!");
        }
    }

    @Transactional
    void update(Long id, StockPlaceRequestDto stockPlaceRequestDto) {
      /*  StockPlace stockPlaceToUpdate = stockPlaceRepository.findById(id).orElseThrow(()
                -> new ValidateException("Места с id = " + id + " не существует!"));*/
        //  void updateById (Long id, StockPlaceRequestDto stockPlaceRequestDto){
        StockPlace stockPlaceToUpdate = getStockPlaceEntityById(id);
        stockPlaceToUpdate.setRow(stockPlaceRequestDto.getRow());
        stockPlaceToUpdate.setShelf(stockPlaceRequestDto.getShelf());
        stockPlaceToUpdate.setCapacity(stockPlaceRequestDto.getCapacity());
        stockPlaceRepository.save(stockPlaceToUpdate);
    }

    //метод для получения StockPlace EntityById + Validate
    private StockPlace getStockPlaceEntityById(Long id) {
        return stockPlaceRepository.getById(id).orElseThrow(()
                -> new ValidateException("Места с id = " + id + " не существует!"));
    }

    //метод для получения Stock EntityById + Validate
    private Stock getStockEntityById(Long stockId) {
        return stockRepository.getById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует!"));
    }
}
