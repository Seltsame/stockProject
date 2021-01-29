package ru.rocketscience.test.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.common.ResponseDto;


@RestController
@RequestMapping(path = "stock")
@Slf4j //включаем логировнаие
@RequiredArgsConstructor
public class StockController {
   /* не надо использовать /, тк он сам ставит.
   При одиночных параметрах можно не ставить path = , если нет др параметров, тк это по умолчанию
   В случае поисковых запросов нужно использовать GET методы + @RequestParam
   */

    private final StockService stockService;

    @GetMapping( "{id}")
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

    //максимальное количество свободных места на складе
    @GetMapping( "maxCapacityInStock/{id}")
    ResponseDto<Long> maxStockCapacity(@PathVariable Long id) {
        log.debug("maxStockCapacity: started with id: {}", id);
        long result = stockService.getStockCapacity(id);
        log.info("maxStockCapacity: finished for id: {} with stock capacity: {}", id, result);
        return new ResponseDto<>(null, result);
    }

    @GetMapping( "searchStock")
    ResponseDto<StockFilterResponseDto> findStockByParam(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "city", required = false) String city) {
        log.debug("filterStock: started with name: {}, and city: {}", name, city);
        StockFilterResponseDto data = stockService.filterStockByParam(name, city);
        log.info("filterStock: finished with data: {}", data);
        return new ResponseDto<>(null, data);
    }

    @GetMapping( "stockListByCityName/{cityName}")
    ResponseDto<StockListResponseDto> getStockListByCityName(@PathVariable String cityName) {
        log.debug("getStockListByCityName: started with city name: {}", cityName);
        StockListResponseDto result = stockService.getStockListByCityName(cityName);
        log.info("getStockListByCityName: finished for city name: {}, with result: {}", cityName, result);
        return new ResponseDto<>(null, result);
    }
  /*  @GetMapping(path = "/stockListByCityName/{cityName}")
   ResponseDto<StockListResponseDto> getStockListByCityName(@PathVariable String cityName) {
        log.debug("getStockListByCityName: started with city name: {}", cityName);
        stockService.getStockListByCityName(cityName);
        log.info("getStockListByCityName: finished for city name: {}, with result: {}", cityName, result);
        return result;
    }*/

    //вывод списка всех складских мест по id склада
    @GetMapping( "allByStockId/{id}")
    ResponseDto<StockResponseDto> getAllByStockId(@PathVariable Long id) {
        log.debug("getAllByStockId: started with id: {}", id);
        StockResponseDto result = stockService.getStockPlaceByStockId(id);
        log.info("getAllByStockId: finished for id: {}, with result: {}", id, result);
        return new ResponseDto<>(null, result);
    }


    //вывод Map id склада - свободное место
    @GetMapping( "/stockPlacesFreeSpaceByStockId/{id}")
    ResponseDto<StockFreeSpaceInMapDto> getStockPlacesFreeSpaceByStockId(@PathVariable Long id) {
        log.debug("getAllFreeSpaceByStockId: started with id: {}", id);
        StockFreeSpaceInMapDto result = stockService.getStockPlacesFreeSpace(id);
        log.info("getAllFreeSpaceByStockId: finished for id: {}, with result: {}", id, result);
        return new ResponseDto<>(null, result);
    }

    @PostMapping
    Long add(@RequestBody StockRequestDto stockRequestDto) {
        log.debug("add: started with data: {}", stockRequestDto);
        Long result = stockService.add(stockRequestDto);
        log.info("add: finished with data: {}", stockRequestDto);
        return result;
    }

    @DeleteMapping( "/{id}")
    void delete(@PathVariable Long id) {
        log.debug("delete: started with: {}", id);
        stockService.delete(id);
        log.info("delete: finished for id: {}", id);
    }

    @PutMapping( "/{id}")
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

