package ru.rocketscience.test.stockPlace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.common.ResponseDto;

@RestController
@RequestMapping(path = "stockplace")
@Slf4j
@RequiredArgsConstructor
public class StockPlaceController {

    private final StockPlaceService stockPlaceService;

    @GetMapping(path = "/{id}")
    @ResponseBody
    ResponseDto<StockPlaceResponseDto> getById(@PathVariable Long id) {
        log.debug("get: Started with id: {}", id);
        StockPlaceResponseDto result = stockPlaceService.getById(id);
        log.info("get: finished for id: {}, with: {}", id, result);
        return new ResponseDto<>(null, result);
    }

    @PostMapping
    @ResponseBody
    Long add(@RequestBody StockPlaceRequestDto stockPlaceRequestDto) {
        return stockPlaceService.add(stockPlaceRequestDto);
    }

    @PostMapping(path = "/addStockPlaces")
    @ResponseBody
    int addStockPlaces(@RequestBody StockPlaceListRequestDto stockPlaceListRequestDto) {
         return stockPlaceService.addStockPlaces(stockPlaceListRequestDto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    void delete(@PathVariable Long id) {
        log.debug("delete: started with id: {}", id);
        stockPlaceService.deleteById(id);
        log.info("delete: finished with id: {}", id );
    }

    @PutMapping(path ="/{id}")
    @ResponseBody
    void update(@PathVariable Long id, @RequestBody StockPlaceRequestDto stockPlaceRequestDto) {
        log.debug("update: started with id: {}", id);
        stockPlaceService.updateById(id, stockPlaceRequestDto);
        log.info("update: finished with id: {}", id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDto<StockPlaceResponseDto> handleValidateException(ValidateException err) {
        String errMessage = err.getMessage();
        log.error("handleValidateException finished with exception: {}", errMessage);
        return new ResponseDto<>(errMessage, null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDto<StockPlaceResponseDto> handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errMessage = ex.getMessage();
        log.error("handleArgumentTypeMismatchException finished with exception: {}", errMessage);
        return new ResponseDto<>("Id места должен быть указан числом! "
                + "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }
}


