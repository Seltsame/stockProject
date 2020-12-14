package ru.rocketscience.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.service.StockService;

@RestController
@RequestMapping(path = "stock")
@Slf4j //включаем логировнаие
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(path = "get/{id}")
    @ResponseBody
    // ResponseDto<StockResponseDto> - через обработчик ошибок пропускаем рабочую DTO
    public ResponseDto<StockResponseDto> getById(@PathVariable Long id) {
        //ставим log.debug(входящие параметры лучше логировать на уровне debug): стартуем get-запрос с id, который попадает сюда
        log.debug("get: started with: {}", id);
        StockResponseDto result = stockService.getById(id);
        log.info("get: finished for id: {} with: {}", id, result); //log.info: выводим результат работы get-запроса
        return new ResponseDto<>(null, result); // если все нормально, отрабатывает StockResponseDto, на выходе имеем result
    }

    //Обработчик ошибок
    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseDto<StockResponseDto> handleValidateException(ValidateException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleValidateException. Ошибки обрабатываются на уровне log.error!!
        log.error("handleValidateException: finished with exception : {}", exMessage);
        //если отрабатывает StockResponseDto, то нам летит ошибка
        return new ResponseDto<>(exMessage, null);

    }

    /* Если ошибка обрабатывается Spring'ом, то надо ее использовать в handler,
    соответственно, вывод сообщения через ResponseDto - text */
    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseDto<StockResponseDto> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String exMessage = ex.getMessage();
        //лог должен начинаться с имени метода! Тут имя метода: handleArgumentTypeMismatchException
        log.error("handleArgumentTypeMismatchException: finished with exception : {}", exMessage);
        //в хорошем error handling'е в ошибке выводится то, что прилетело от клиента: параметр и значение.
        return new ResponseDto<>("Номер склада должен быть указан числом! " + "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }
}

