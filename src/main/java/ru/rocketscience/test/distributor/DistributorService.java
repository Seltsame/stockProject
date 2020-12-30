package ru.rocketscience.test.distributor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.stock.Stock;
import ru.rocketscience.test.stock.StockMapper;
import ru.rocketscience.test.stock.StockRepository;
import ru.rocketscience.test.stock.StockResponseDto;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceMapper;
import ru.rocketscience.test.stockPlace.StockPlaceRepository;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
        //переделай тесты!
class DistributorService {

    private final StockPlaceRepository stockPlaceRepository;
    private final StockRepository stockRepository;
    private final StockPlaceMapper stockPlaceMapper;
    private final StockMapper stockMapper;

    //поиск и вывод списка складов по городу
    List<StockResponseDto> getCitiesListByName(String cityName) {
        if (cityName.isEmpty()) {
            throw new ValidateException("Нельзя не выбрать пустой город!");
        }
        return stockRepository.findAllByCity(cityName).stream()
                .map(stock -> stockMapper.fromEntity(stock))
                .collect(Collectors.toList());
    }

    //поиск места по ид склада
    public List<StockPlaceResponseDto> getStockPlaceByStockId(Long id) {
        return stockPlaceRepository.findAllById(id).stream()
                .map(stockPlace -> stockPlaceMapper.fromEntity(stockPlace))
                .collect(Collectors.toList());
    }

    //вывод максимального количества свободных мест на складе
    //ТЕСТА НА ЭТО ЕЩЕ НЕТ
    public int getMaxStockCapacity(Long id) {

        int maxCapacity = 0;
        List<StockPlace> allByStockId = stockPlaceRepository.findAllByStockId(id);

        for (StockPlace stockPlace : allByStockId) {
            maxCapacity = stockPlace.getCapacity();
            maxCapacity += maxCapacity;
        }
        return maxCapacity;
    }

    //место-остаток в ДТО место - остаток
    //ТЕСТА НЕТ!!!
    @Transactional
    public DistributorPlCapResponseDto getStockPlaceCapacity(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + stockId + " не существует"));
        StockResponseDto stockResponseDto = stockMapper.fromEntity(stock);

        StockPlaceResponseDto stockPlaceResponseDto
                = stockResponseDto.getStockPlaceResponseDto();

        return DistributorPlCapResponseDto.builder()
                .stockPlaceId(stockPlaceResponseDto.getId())
                .rowName(stockPlaceResponseDto.getRow())
                .shelfCapacity(stockPlaceResponseDto.getShelf())
                .shelfCapacity(stockPlaceResponseDto.getCapacity())
                .build();
    }

    //ТЕСТА НЕТ
    //Максимально свободная полка
    public int freestShelf(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(()
                -> new ValidateException("Склада с таким id: " + id + " не существует"));
        return 0;
    }

}

