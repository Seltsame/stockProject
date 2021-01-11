package ru.rosketscience.test.distributor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.rosketscience.test.stock.StockResponseDto;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/distributor")
@Slf4j
@RequiredArgsConstructor
public class DistributorController {

    /*private final DistributorService distributorService;

    @GetMapping(path = "getAllStocks")
    List<StockResponseDto> getCitiesList(@RequestParam String cityName) {
        log.debug("getCitiesList: started with: {}", cityName);
        List<StockResponseDto> result = distributorService.getStocksListByCityName(cityName);
        log.info("getCitiesList: finished with: {}, {}", result, cityName); //уточнить про логирование, тк в result - список
        // return new ResponseDto<>(null, result); //??
        return result;
    }

    @GetMapping(path = "{id}")
    List<StockPlaceResponseDto> getStockPlaceByStockId(@PathVariable Long id) {
        log.debug("getStockPlaceByStockId: started with id: {}", id);
        List<StockPlaceResponseDto> result = distributorService.getStockPlaceByStockId(id);
        log.info("getStockPlaceByStockId: finished with id: {}, {}", id, result); //уточнить про логирование, тк в result - список
        //return new ResponseDto<>(null, result);//??
        return result;
    }

    @GetMapping(path = "stockPlaceCapacity/{id}")
    DistributorPlCapResponseDto getStockPlaceCapacity(@PathVariable Long id) {
        log.debug("getStockPlaceCapacity: started with id: {}", id);
        return distributorService.getStockPlaceCapacity2(id);
    }

    @GetMapping(path = "maxStockCapacity/{id}")
    int getMaxStockCapacity(@PathVariable Long id) {
        log.debug("getMaxStockCapacity: started with id: {}", id);
        return distributorService.getStockCapacity(id);
    }*/

}
