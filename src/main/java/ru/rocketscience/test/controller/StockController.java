package ru.rocketscience.test.controller;

import org.springframework.web.bind.annotation.*;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.service.StockService;

@RestController
@RequestMapping(path = "stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(path = "get/{id}")
    @ResponseBody
    public StockResponseDto getById(@PathVariable Long id) {
        return stockService.getById(id);
    }
}
