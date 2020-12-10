package ru.rocketscience.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public StockResponseDto getById(@PathVariable Long id) {
        log.debug("get: started with: {}", id); //ставим log.debug(входящие параметры лучше логировать на уровне debug): стартуем get-запрос с id, который попадает сюда
        StockResponseDto result = stockService.getById(id);
        log.info("get: finished for id: {} with: {}", id, result); //log.info: выводим результат работы get-запроса
        return result;
    }
}

