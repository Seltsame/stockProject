package ru.rocketscience.test.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceMapper;
import ru.rocketscience.test.stockPlace.StockPlaceRepository;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class StockService {

    private final StockMapper stockMapper;
    private final StockRepository stockRepository;
    private final StockPlaceMapper stockPlaceMapper;
    private final StockSpecification stockSpecification;
    private final StockPlaceRepository stockPlaceRepository;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;

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
        return stockMapper.fromEntity(getStockEntityById(id));
    }

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
        Stock stockToUpdate = getStockEntityById(id);
        //пишем напрямую в сущность новые пришедшие данные
        stockToUpdate.setName(stockRequestDto.getName());
        stockToUpdate.setCity(stockRequestDto.getCity());
        stockRepository.save(stockToUpdate); //
    }

    //вывод максимального количества свободных мест на складе
    @Transactional
    Long getStockCapacity(Long id) {
        Stock stock = getStockEntityById(id);
        Long stockPlaceId = stock.getId();
        return stockPlaceRepository.getSumStockPlaceCapacity(stockPlaceId) -
                productOnStockPlaceRepository.getSumQuantityProductByStockId(stockPlaceId);
    }

    //поиск и вывод списка складов по городу
    @Transactional
    StockListResponseDto getStockListByCityName(String cityName) {
        if (cityName.isEmpty()) {
            throw new ValidateException("Нельзя выбрать пустой город!");
        }
        List<String> stockNameList = stockRepository.findAllByCityOrderByName(cityName).stream()
                .map(stockMapper::fromEntity) //stock -> stockMapper.fromEntity(stock)
                .map(StockResponseDto::getName)
                .collect(Collectors.toList());
        return new StockListResponseDto(stockNameList);
    }
/*    @Transactional
    List<String> getStockListByCityName(String cityName) {
        if (cityName.isEmpty()) {
            throw new ValidateException("Нельзя выбрать пустой город!");
        }
        return stockRepository.findAllByCityOrderByName(cityName).stream()
                .map(stockMapper::fromEntity) //stock -> stockMapper.fromEntity(stock)
                .map(StockResponseDto::getName)
                .collect(Collectors.toList());
    }*/

    //поиск по склада по имени города и/или склада
    @Transactional
    StockFilterResponseDto filterStockByParam(String name, String city) {
        List<StockResponseDto> stockListByParam
                = stockRepository.findAll(stockSpecification.findByNameAndCity(name, city))
                .stream()
                .map(stockMapper::fromEntity)
                .collect(Collectors.toList());
        return StockFilterResponseDto.builder()
                .stockList(stockListByParam)
                .build();
    }


    //поиск мест по ид склада вывод в map х : у
  /*  @Transactional
    public StockFreeSpaceDto getStockPlacesFreeSpace(Long stockId) {
        //проверяем склад на существование
        getStockEntityById(stockId);

        Map<Long, Long> stockPlaceCapacityByStockId = new HashMap<>();
        Set<StockPlace> allByStockIdList = stockPlaceRepository.findAllByStockId(stockId);
        allByStockIdList.forEach(stockPlace -> {
            Long id = stockPlace.getId();
            long capacity = stockPlace.getCapacity();
            stockPlaceCapacityByStockId.put(id, capacity);
        });
        *//* Как вариант - можно сразу подготовить Query-запрос с выводом в Map и работать с ним. См. Query в ProductOnStockPlaceRepository
        с Pair of *//*
        //= stockPlaceRepository.getStockPlaceIdAndCapacityByStockId(stockId);
       *//* Map<Long, Long> stockPlaceQuantity = productOnStockPlaceRepository.getQuantityProductOnStockPlaceByStockId(stockId)
                .collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getKey()));*//*
        Map<Long, Long> stockPlaceQuantity
                = productOnStockPlaceRepository.getQuantityProductOnStockPlaceByStockId(stockId)
                .stream()
                .collect(Collectors.toMap(ProductOnStockPlace::getId, ProductOnStockPlace::getQuantityProduct));

        Map<Long, Long> stockPlaceFreeSpace = new HashMap<>();

        for (Map.Entry<Long, Long> entry : stockPlaceCapacityByStockId.entrySet()) {
            long resultFreeSpace = entry.getValue() - stockPlaceQuantity.getOrDefault(entry.getKey(), 0L);
            if (resultFreeSpace > 0) {
                stockPlaceFreeSpace.put(entry.getKey(), resultFreeSpace);
            }
        }
        return StockFreeSpaceDto.builder()
                .stockPlaceIdFreeSpaceByStockId(stockPlaceFreeSpace)
                .build();
    }*/

    //поиск мест по ид склада вывод в DTO: id место порядковый номер полочки и ряд полочки
    @Transactional
    List<StockFreeSpaceDto> getStockPlacesFreeSpace(Long stockId) {
        //проверяем склад на существование
        getStockEntityById(stockId);
        List<StockFreeSpaceDto> result = new ArrayList<>();
        StockFreeSpaceDto.StockFreeSpaceDtoBuilder current = StockFreeSpaceDto.builder();
        Set<ProductOnStockPlace> quantityProduct = productOnStockPlaceRepository.getProductOnStockPlaceByStockId(stockId);

//берем пару из сета и сравниваем с мапой //key мап использовать пару product id + stockPlace - id/ value - quantity продукт
        //если такой же id существет, то складываем количество товара и кладём в мапу
        Map<Long, Long> spQuantity
                = quantityProduct
                .stream()
                .collect(Collectors.toMap(pr -> pr.getStockPlace().getId(), ProductOnStockPlace::getQuantityProduct,
                        (existing, anotherExisting) -> existing + anotherExisting)); //Long::sum
        //При конфликте Keys в данном случае, он складывает Values


        Set<StockPlace> allByStockIdList = stockPlaceRepository.findAllByStockId(stockId);
        allByStockIdList.forEach(stockPlace -> {
            current.id(stockPlace.getId());
            current.row(stockPlace.getRow());
            current.shelf(stockPlace.getShelf());
            long freeSpace = 0L;
            for (Map.Entry<Long, Long> entry : spQuantity.entrySet()) {
                if (entry.getKey().equals(stockPlace.getId())) {
                    if (entry.getValue() != null) {
                        freeSpace = stockPlace.getCapacity() - entry.getValue();
                    } else {
                        freeSpace = stockPlace.getCapacity();
                    }
                    current.freeSpace(freeSpace);
                }
            }
            result.add(current.build());
        });
        return result;
    }

    //альтернативный вариант решения, не самый правильный, тк часто дергаем бд в ForEach
    //если не использовать custom query запрос
    //берем сет товаров из репозитория по ID склада -> Складское место, с помощью custom @Query запроса
        /*Map<Long, Long> stockPlaceCapacity = new HashMap<>();
        List<StockPlace> allByStockIdList = stockPlaceRepository.findAllByStockId(stockId);
        allByStockIdList.forEach(stockPlace -> {
            Long id = stockPlace.getId();
            long capacity = stockPlace.getCapacity();
            stockPlaceCapacity.put(id, capacity);
        });*/
    //если будет null, То getOrDefault вернет 0

    //с помощью кастомного Query запроса сразу пишем stockPlaceId + Capacity в map
       /*
       + см запрос:

    @Query("FROM ProductOnStockPlace psp JOIN StockPlace sp ON sp.id = psp.stockPlace.id where sp.id =:stock_id")
    Set<ProductOnStockPlace> getProductOnStockPlaceByStockId2(@Param("stock_id") Long stock_id);
       и развертка сета

       Set<ProductOnStockPlace> getProductOnStockPlaceByStockId
               = productOnStockPlaceRepository.getProductOnStockPlaceByStockId2(stockId);
        Map<Long, Long> productQuantityInStock = new HashMap<>();
        getProductOnStockPlaceByStockId.forEach(productOnStockPlace -> {
            Long stockPlaceId = productOnStockPlace.getStockPlace().getId();
            Long quantityProductInStockPlace = productOnStockPlace.getQuantityProduct();
            productQuantityInStock.put(stockPlaceId, quantityProductInStockPlace);
        });

        Map<Long, Long> freeSpaceInStock = new HashMap<>();
        freeSpaceInStock.forEach((k, v) -> {

        });

        Set<ProductOnStockPlace> allByStockId
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockId2(stockId);
        //выводим в HashMap: id stockPlace : freeSpace
        Map<Long, Long> freeSpaceByStockId = new HashMap<>();
        allByStockId.forEach(productOnStockPlace -> {
            //freeSpace рассчитывается исходя из количества товара и свободного места неа полке

            Long stockPlaceId = productOnStockPlace.getStockPlace().getId();
            long maxQuantityProductOnStockPlace
                    = productOnStockPlaceRepository.getSumQuantityProduct(stockPlaceId);
            long freeSpace = stockPlaceRepository.getSumStockPlaceCapacity(stockPlaceId)
                    - maxQuantityProductOnStockPlace;
            if (freeSpace > 0) {
                freeSpaceByStockId.put(productOnStockPlace.getStockPlace().getId(), freeSpace);
            }
        });*/

    //поиск и вывод в list всех мест по ид склада
    StockResponseDto getStockPlaceByStockId(Long id) {
        getStockEntityById(id);
        List<StockPlace> stockPlacesByStockId = stockPlaceRepository.findStockPlacesByStockId(id);
        List<StockPlaceResponseDto> collect = stockPlacesByStockId.stream()
                .map(stockPlace -> stockPlaceMapper.fromEntity(stockPlace))
                .collect(Collectors.toList());
        return StockResponseDto.builder()
                .stockPlaceList(collect)
                .build();
    }

    //метод для получения EntityById + Validate
    private Stock getStockEntityById(Long stockId) {
        return stockRepository.getById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
    }
}