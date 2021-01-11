package ru.rosketscience.test.stockPlace;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rosketscience.test.stock.Stock;
import ru.rosketscience.test.stock.StockRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockPlaceService {

    private final StockPlaceRepository stockPlaceRepository;
    private final StockRepository stockRepository;
    private final StockPlaceMapper stockPlaceMapper;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;

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
    int addStockPlaces(StockPlaceBunchRequestDto stockPlaceBunchRequestDto) {
        //из бд берем максимальный порядковый номер полочки
        //нужен ли поиск в конкретном ряду, если ряд приходит в DTO?
        int maxShelfNumber = stockPlaceRepository.getMaxShelfNumber(stockPlaceBunchRequestDto.getRowName());
        int firstAddedShelfNum = 0;
        //создаём объект StockPlace и присваиваем данные из DTO
        StockPlace.StockPlaceBuilder builder = StockPlace.builder()
                .row(stockPlaceBunchRequestDto.getRowName())
                .capacity(stockPlaceBunchRequestDto.getShelfCapacity())
                .shelf(stockPlaceBunchRequestDto.getShelfNumber());

        //создаём Entity, записываем ее в бд. (в цикле делается для того, чтобы записать пачку идекнтичных полочек)
        for (int i = 1; i <= stockPlaceBunchRequestDto.getShelfNumber(); i++) {
            StockPlace entityToSave = builder.build();
            Stock stock
                    = stockRepository.getById(stockPlaceBunchRequestDto.getStockId()).orElseThrow(()
                    -> new ValidateException("Места с таким id не существует!"));
            //присваиваем id необходимого склада
            entityToSave.setStock(stock);
            //присваиваем порядковый номер полочки + 1, чтобы в бд полочки шли по порядку
            entityToSave.setShelf(maxShelfNumber + i);
            stockPlaceRepository.save(entityToSave);
        }
        //по тз надо вернуть номер первого добавленного места
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

    /*//вывод полка - свободное место
    @Transactional
    public StockCapacityDto getStockPlacesFreeSpace(Long stockId) {
        stockRepository.findById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
        Set<ProductOnStockPlace> allByStockPlaceId
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockId(stockId);
        Map<Long, Integer> freeSpaceByStockId = new HashMap<>();
        allByStockPlaceId.forEach(productOnStockPlace -> {
            int freeSpace = productOnStockPlace.getStockPlace().getCapacity()
                    - productOnStockPlace.getQuantityProduct();
            freeSpaceByStockId.put(productOnStockPlace.getStockPlace().getId(), freeSpace);
        });
        return StockCapacityDto.builder()
                .freeSpaceByStockId(freeSpaceByStockId)
                .build();
    }*/

    //поиск мест по ид склада
    public StockPlaceResponseDto getStockPlaceByStockId(Long id) {
        List<StockPlaceResponseDto> stockPlaceList = stockPlaceRepository.findAllByIdOrderByShelf(id).stream()
                .map(stockPlaceMapper::fromEntity)
                .collect(Collectors.toList());
        return StockPlaceResponseDto.builder()
                .stockPlaceList(stockPlaceList)
                .build();
    }

}
