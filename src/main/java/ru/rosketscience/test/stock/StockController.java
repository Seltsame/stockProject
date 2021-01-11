package ru.rosketscience.test.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.common.ResponseDto;
import ru.rosketscience.test.stockPlace.StockPlaceResponseDto;


@RestController
@RequestMapping(path = "stock")
@Slf4j //включаем логировнаие
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping(path = "/{id}")
        //@ResponseBody
        // ResponseDto<StockResponseDto> - через спец DTO (которая разделяется на err и data) пропускаем рабочую DTO
    ResponseDto<StockResponseDto> getById(@PathVariable Long id) {
        //ставим log.debug(входящие параметры лучше логировать на уровне debug):
        // стартуем get-запрос с id, который попадает сюда
        log.debug("get: started with: {}", id);
        StockResponseDto result = stockService.getById(id);
        log.info("get: finished for id: {} with: {}", id, result); //log.info: выводим результат работы get-запроса
        return new ResponseDto<>(null, result); // если все нормально, отрабатывает StockResponseDto,
        // на выходе имеем result, err == null
    }

    //напиши тест!!!
    @GetMapping(path = "/maxCapacityStock/{id}")
    int maxStockCapacity(@PathVariable Long id) {
        log.debug("maxStockCapacity: started with id: {}", id);
        int stockCapacity = stockService.getStockCapacity(id);
        log.info("maxStockCapacity: finished for id: {} with stock capacity: {}", id, stockCapacity);
        return stockCapacity;
    }

    //напиши тест!!!
    @GetMapping(path = "/stockListByCityName/{cityName}")
    ResponseDto<StockResponseDto> getStockListByCityName(@PathVariable String cityName) {
        log.debug("getStockListByCityName: started with city name: {}", cityName);
        StockResponseDto result = stockService.getStockListByCityName(cityName);
        log.info("getStockListByCityName: finished for city name: {}, with result: {}", cityName, result);
        return new ResponseDto<>(null, result);
    }

    //напиши тест!!!
    @GetMapping(path = "/allByStockId/{id}")
    ResponseDto<StockListStockPlaceDto> getAllByStockId(@PathVariable Long id) {
        log.debug("getAllByStockId: started with id: {}", id);
        StockListStockPlaceDto result = stockService.getStockPlaceByStockId(id);
        log.info("getAllByStockId: finished for id: {}, with result: {}", id, result);
        return new ResponseDto<>(null, result);
    }

    //напиши тест!!!
    @GetMapping(path = "/allFreeSpace/{id}")
    ResponseDto<StockCapacityDto> getAllFreeSpaceByStockId(@PathVariable Long id) {
        log.debug("getAllFreeSpaceByStockId: started with id: {}", id);
        StockCapacityDto result = stockService.getStockPlacesFreeSpace(id);
        log.info("getAllFreeSpaceByStockId: finished for id: {}, with result: {}", id, result);
        return new ResponseDto<>(null, result);
    }

    @PostMapping
    Long add(@RequestBody StockRequestDto stockRequestDto) {
        return stockService.add(stockRequestDto);
    }

    @DeleteMapping(path = "/{id}")
    void delete(@PathVariable Long id) {
        log.debug("delete: started with: {}", id);
        stockService.delete(id);
        log.info("delete: finished for id: {}", id);
    }

    @PutMapping(path = "/{id}")
    void update(@RequestBody StockRequestDto stockRequestDto, @PathVariable Long id) {
        log.debug("update: started with: {}", id);
        stockService.update(id, stockRequestDto);
        log.info("update: finished for id: {}", id);
    }

    /*
     * Обработчик ошибок
     * Ошибка взятия несуществующего ID */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //Использование Bad request - самое оптимальное в handler
    ResponseDto<StockResponseDto> handleValidateException(ValidateException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleValidateException.
        // Ошибки обрабатываются на уровне log.error!!
        log.error("handleValidateException: finished with exception : {}", exMessage);
        //если отрабатывает StockResponseDto, то нам летит ошибка, дата, соотв. null
        return new ResponseDto<>(exMessage, null);
    }

    /* Если ошибка обрабатывается Spring'ом, то надо ее использовать в handler,
     * соответственно, вывод сообщения через ResponseDto - text
     * Обработка ошибки написания ID числом */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //Использование Bad request - самое оптимальное в handler
    ResponseDto<StockResponseDto> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleArgumentTypeMismatchException
        log.error("handleArgumentTypeMismatchException: finished with exception : {}", exMessage);
        //в хорошем error handling'е в ошибке выводится то, что прилетело от клиента: параметр и значение.
        return new ResponseDto<>("ID склада должен быть указан числом! " +
                "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }
}

