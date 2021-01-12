package ru.rosketscience.test.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.common.ResponseDto;

@RestController
@RequestMapping(path = "product")
@Slf4j //включаем логировнаие
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(path = "{id}")
    // ResponseDto<StockResponseDto> - через спец DTO (которая разделяется на err и data) пропускаем рабочую DTO
    public ResponseDto<ProductResponseDto> getById(@PathVariable Long id) {
        log.debug("get: started with id: {}", id);
        ProductResponseDto result = productService.getById(id);
        log.info("get: finished for id: {}, with result: {}", id, result);
        return new ResponseDto<>(null, result); //если ошибки нет, то возвращается result
    }

    @PostMapping
    Long add(@RequestBody ProductRequestDto productRequestDto) {
        log.debug("add: started with data: {}", productRequestDto);
        Long result = productService.add(productRequestDto);
        log.info("add: finished for with result: {}", result);
        return result;
    }

    @PostMapping(path = "/addProducts")
    @ResponseBody
    void addProducts(@RequestBody ProductPlacementDto productPlacementDto) {
        log.debug("addProducts: started with data: {}", productPlacementDto);
        productService.addProductsToStockPlace(productPlacementDto);
        log.info("addProducts: finished for data: {}", productPlacementDto);
    }

    @DeleteMapping(path = "{id}")
    void delete(@PathVariable Long id) {
        log.debug("delete: started with id: {}", id);
        productService.delete(id);
        log.info("delete: finished for id: {}", id);
    }

    @PutMapping(path = "{id}")
    void update(@RequestBody ProductRequestDto productRequestDto, @PathVariable Long id) {
        log.debug("update: started with id: + {}", id);
        productService.update(id, productRequestDto);
        log.info("update: finished for id: {}", id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDto<ProductResponseDto> handlerValidateExceptionProduct(ValidateException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerValidateExceptionProduct: finished with exception: + {}", errMessage);
        return new ResponseDto<>(errMessage, null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDto<ProductResponseDto> handlerArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerArgumentTypeMismatchException: finished with exception: + {}", errMessage);
        return new ResponseDto<>("ID товара должен быть указан числом! "
                + "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }
}


