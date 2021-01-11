package ru.rosketscience.test.distributor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rosketscience.test.stock.*;
import ru.rosketscience.test.stockPlace.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
        //переделай тесты!
class DistributorService {
/*
    private final StockPlaceRepository stockPlaceRepository;
    private final StockRepository stockRepository;
    private final StockPlaceMapper stockPlaceMapper;
    private final StockMapper stockMapper;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;


    // ++
    //поиск и вывод списка складов по городу
    List<StockResponseDto> getStocksListByCityName(String cityName) {
        if (cityName.isEmpty()) {
            throw new ValidateException("Нельзя не выбрать пустой город!");
        }
        return stockRepository.findAllByCityOrderByName(cityName).stream()
                .map(stock -> stockMapper.fromEntity(stock))
                .collect(Collectors.toList());
    }

    //++
    //поиск мест по ид склада
    public List<StockPlaceResponseDto> getStockPlaceByStockId(Long id) {
        return stockPlaceRepository.findAllByIdOrderByShelf(id).stream()
                .map(stockPlace -> stockPlaceMapper.fromEntity(stockPlace))
                .collect(Collectors.toList());
    }

    //++
    //вывод оличества свободных мест на складе
    //ТЕСТА НА ЭТО ЕЩЕ НЕТ
    public int getStockCapacity(Long id) {

        int maxCapacity = 0;
        List<StockPlace> allByStockId = stockPlaceRepository.findAllByStockId(id);

        for (StockPlace stockPlace : allByStockId) {
            maxCapacity = stockPlace.getCapacity();
            maxCapacity += maxCapacity;
        }
        return maxCapacity;
    }

    //место-остаток в ДТО место - остаток
    //ТЕСТА НЕТ!!! //взял только одну полку?
    @Transactional
    public DistributorPlCapResponseDto getStockPlaceCapacity2(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
        StockResponseDto stockResponseDto = stockMapper.fromEntity(stock);

     ;

        return DistributorPlCapResponseDto.builder()
                .stockPlaceId(stockPlaceResponseDto.getId())
                .rowName(stockPlaceResponseDto.getRow())
                .shelfCapacity(stockPlaceResponseDto.getCapacity())
                .build();
    }

    //++
    //вывод полка - свободное место  через Set?
 КАК раз метод вывода в DTO id складского места и остатка свободного места
     *

     * JSON
     * {
     * 1 : 3
     * 2 : 6
     * 3 : 0
     * }
     *

    @Transactional
    public StockCapacityDto getStockPlaceCapacity(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
        StockResponseDto stockResponseDto = stockMapper.fromEntity(stock);
Set<StockPlace> stockPlacesSet = stockPlaceRepository.findAllByStockIdSet(stockId);

Set<ProductOnStockPlace> allByStockPlaceId
                = productOnStockPlaceRepository.findAllByStockPlaceId(stockPlacesSet.stream()
                .map(StockPlace::getId).collect(Collectors.toSet()));

        Set<ProductOnStockPlace> allByStockPlaceId
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockId(stockId);
        Map<Long, Integer> capacityByStockId = new HashMap<>();
        allByStockPlaceId.forEach(productOnStockPlace -> {
            int freeSpace = productOnStockPlace.getStockPlace().getCapacity() - productOnStockPlace.getQuantityProduct();
            capacityByStockId.put(productOnStockPlace.getStockPlace().getId(), freeSpace);
        });
        return StockCapacityDto.builder()
                .freeSpaceByStockId(capacityByStockId)
                .build();

Set<DistributorResponseQuantityDto> productOnStockPlaces = new HashSet<>(stockPlacesSet.size());
        for (StockPlace stockPlace : stockPlacesSet) {
            int freeSpace = stockPlaceRepository.findProductQuantity(stockPlace.getId())
                    - stockResponseDto.getStockPlaceResponseDto().getCapacity();
            productOnStockPlaces.add(new DistributorResponseQuantityDto(stockPlace.getId(), freeSpace));
        }

        return productOnStockPlaces;

    }*/
}

