package ru.rocketscience.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;
import ru.rocketscience.test.service.StockService;


@RestController
@RequestMapping(path = "stock")
@Slf4j //включаем логировнаие
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    // ResponseDto<StockResponseDto> - через спец DTO (которая разделяется на err и data) пропускаем рабочую DTO
    public ResponseDto<StockResponseDto> getById(@PathVariable Long id) {
        //ставим log.debug(входящие параметры лучше логировать на уровне debug): стартуем get-запрос с id, который попадает сюда
        log.debug("get: started with: {}", id);
        StockResponseDto result = stockService.getStockById(id);
        log.info("get: finished for id: {} with: {}", id, result); //log.info: выводим результат работы get-запроса
        return new ResponseDto<>(null, result); // если все нормально, отрабатывает StockResponseDto, на выходе имеем result, err == null
    }

    @PostMapping
    public Long addStock(@RequestBody StockRequestDto stockRequestDto) {
        return stockService.addStock(stockRequestDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteStock(@PathVariable Long id) {
        log.debug("delete: started with: {}", id);
        stockService.deleteStock(id);
        log.info("delete: finished for id: {}", id);
    }

    @PutMapping(path = "/{id}")
    public void updateStock(@RequestBody StockRequestDto stockRequestDto, @PathVariable Long id) {
        log.debug("update: started with: {}", id);
        stockService.updateStock(id, stockRequestDto);
        log.info("update: finished for id: {}", id);
    }

    /*
     * Обработчик ошибок
     * Ошибка взятия несуществующего ID */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Использование Bad request - самое оптимальное в handler
    public ResponseDto<StockResponseDto> handleValidateException(ValidateException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleValidateException. Ошибки обрабатываются на уровне log.error!!
        log.error("handleValidateException: finished with exception : {}", exMessage);
        //если отрабатывает StockResponseDto, то нам летит ошибка, дата, соотв. null
        return new ResponseDto<>(exMessage, null);
    }

    /* Если ошибка обрабатывается Spring'ом, то надо ее использовать в handler,
     * соответственно, вывод сообщения через ResponseDto - text
     * Обработка ошибки написания ID числом */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Использование Bad request - самое оптимальное в handler
    public ResponseDto<StockResponseDto> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleArgumentTypeMismatchException
        log.error("handleArgumentTypeMismatchException: finished with exception : {}", exMessage);
        //в хорошем error handling'е в ошибке выводится то, что прилетело от клиента: параметр и значение.
        return new ResponseDto<>("ID склада должен быть указан числом! " +
                "Ошибка ввода в: " + ex.getParameter().getParameterName() + ", со значением value: " + ex.getValue(), null);
    }
}

