package ru.rosketscience.test.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rosketscience.test.stockPlace.StockPlace;
import ru.rosketscience.test.stockPlace.StockPlaceMapper;
import ru.rosketscience.test.stockPlace.StockPlaceRepository;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class StockService {

    private final StockRepository stockRepository;
    private final StockPlaceRepository stockPlaceRepository;
   // private final ProductOnStockPlace productOnStockPlace;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;
    private final StockMapper stockMapper;
    private final StockPlaceMapper stockPlaceMapper;


     /* Также getById() можно через Optional Лезем в репозиторий, чтобы достать сущность:
       Optional<Stock> entityToGet = stockRepository.findById(id);
        if (!entityToGet.isPresent()) { //проверяем на существование
            throw new ValidateException("Склада с id = " + id + " не существует");
        } //возвращаем DTO сущности
        return stockMapper.fromEntity(entityToGet.get());
        */

    // Также можно и через Optional: Лезем в репозиторий, чтобы достать сущность
/*        Optional<Stock> entityToUpdate = stockRepository.findById(id);
        if (!entityToUpdate.isPresent()) {
            throw new ValidateException("Склада с id = " + id + " не существует");
        }
        //пишем напрямую в сущность новые пришедшие данные
        entityToUpdate.get().setCity(stockRequestDto.getCity());
        entityToUpdate.get().setName(stockRequestDto.getName());
        stockRepository.save(entityToUpdate.get()); //entityToUpdate.get() - как раз Entity. Сохраняем в бд*/

    StockResponseDto getById(Long id) {
        Stock stockById = stockRepository.findById(id).orElseThrow(()
                -> new ValidateException("Склада с id = " + id + " не существует!"));
        return stockMapper.fromEntity(stockById);
    }

    //НУЖЕН ЛИ @Transactional?
    //возвращаем ID после записи в репозиторий(если это нужно, если нет - void)
    Long add(StockRequestDto stockRequestDto) {
        Stock addStock = stockRepository.save(stockMapper.toEntity(stockRequestDto));
        return stockMapper.fromEntity(addStock).getId();
    }

    @Transactional
    void delete(Long id) {
        try {
            stockRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) { //При отсутствии в бд id удаляемой сущности, будет выброшена следующая ошибка:
            throw new ValidateException("Склада с id = " + id + " не существует!");
        }
    }

    @Transactional
    void update(Long id, StockRequestDto stockRequestDto) {
        //через orElseTrow() напрямую тащим сущность из бд, если ее нет - пробрасываем кастомную ошибку
        Stock stockToUpdate = stockRepository.findById(id).orElseThrow(()
                -> new ValidateException("Склада с id = " + id + " не существует!"));
        //пишем напрямую в сущность новые пришедшие данные
        stockToUpdate.setName(stockRequestDto.getName());
        stockToUpdate.setCity(stockRequestDto.getCity());
        stockRepository.save(stockToUpdate); //
    }


    //на это теста ещё нет!!!
    //смотри сюда!!!
    //вывод максимального количества мсест на складе - не свободных, пока что, просто мест, без привязки к количеству продукта!
    @Transactional
    public int getStockCapacity(Long id) {
        int maxCapacity = 0;
        List<StockPlace> allByStockId = stockPlaceRepository.findAllByStockId(id);
        /*int freeSpace2 = productOnStockPlace.getStockPlace().getCapacity()
                - productOnStockPlace.getQuantityProduct();*/
        int freeSpace = stockPlaceRepository.getStockPlaceCapacity(id)
                - productOnStockPlaceRepository.getMaxQuantityProduct(id);
        for (StockPlace stockPlace : allByStockId) {
            maxCapacity = stockPlace.getCapacity() - freeSpace;
            maxCapacity += maxCapacity;
        }
        return maxCapacity;
    }

    //теста нет!
    //поиск и вывод списка складов по городу
    @Transactional
    StockResponseDto getStockListByCityName(String cityName) {
        if (cityName.isEmpty()) {
            throw new ValidateException("Нельзя выбрать пустой город!");
        }
        List<String> stockNameList = stockRepository.findAllByCityOrderByName(cityName).stream()
                .map(stockMapper::fromEntity) //stock -> stockMapper.fromEntity(stock)
                .map(StockResponseDto::getName)
                .collect(Collectors.toList());
        return StockResponseDto.builder()
                .stockList(stockNameList)
                .build();
    }

    //вывод полка - свободное место
    @Transactional
    public StockCapacityDto getStockPlacesFreeSpace(Long stockId) {
        //проверяем склад на существование
        stockRepository.findById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
        //берем сет товаров из репозитория по ID склада -> Складское место, с помощью custom @Query запроса
        Set<ProductOnStockPlace> allByStockPlaceId
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockId(stockId);
        //выводим в HashMap: id stockPlace : freeSpace
        Map<Long, Integer> freeSpaceByStockId = new HashMap<>();
        allByStockPlaceId.forEach(productOnStockPlace -> {
            //freeSpace рассчитывается исходя из количества товара и свободного места неа полке

            //как верно из двух ниже?
            /*
            int maxQuantityProductOnStockPlace
                    = productOnStockPlaceRepository.getMaxQuantityProduct(productOnStockPlace.getStockPlace().getId());
            int freeSpace = productOnStockPlace.getStockPlace().getCapacity()
                    - maxQuantityProductOnStockPlace;
                    */
            int freeSpace = productOnStockPlace.getStockPlace().getCapacity()
                    - productOnStockPlace.getQuantityProduct();
            freeSpaceByStockId.put(productOnStockPlace.getStockPlace().getId(), freeSpace);
        });
        return StockCapacityDto.builder()
                .freeSpaceByStockId(freeSpaceByStockId)
                .build();
    }

    //поиск мест по ид склада
    public StockListStockPlaceDto getStockPlaceByStockId(Long id) {
        List<StockPlaceResponseDto> stockPlaceList = stockPlaceRepository.findAllByIdOrderByShelf(id).stream()
                .map(stockPlaceMapper::fromEntity)
                .collect(Collectors.toList());
        return StockListStockPlaceDto.builder()
                .stockPlaceList(stockPlaceList)
                .build();
    }


}


