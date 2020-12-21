package ru.rocketscience.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.request.ProductRequestDto;
import ru.rocketscience.test.service.ProductService;

@RestController
@RequestMapping(path = "product")
@Slf4j //включаем логировнаие
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "{id}")
    @ResponseBody
    // ResponseDto<StockResponseDto> - через спец DTO (которая разделяется на err и data) пропускаем рабочую DTO
    public ResponseDto<ProductResponseDto> getById(@PathVariable Long id) {
        log.debug("get: started with: {}", id);
        ProductResponseDto result = productService.getProductById(id);
        log.info("get: finished with: {}, {}", id, result);
        return new ResponseDto<>(null, result); //если ошибки нет, то возвращается result
    }

    @PostMapping
    public Long addProduct(@RequestBody ProductRequestDto productRequestDto) {
        return productService.addProduct(productRequestDto);
    }

    @DeleteMapping(path = "{id}")
    public void deleteProduct(@PathVariable Long id) {
        log.debug("delete: started with: {}", id);
        productService.deleteProduct(id);
        log.info("delete: finished with: {}", id);
    }

    @PutMapping(path = "{id}")
    public void updateProduct(@RequestBody ProductRequestDto productRequestDto, @PathVariable Long id) {
        log.debug("update: starter with: + {}", id);
        productService.updateProduct(id, productRequestDto);
        log.info("update: finished for id: {}", id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<ProductResponseDto> handlerValidateExceptionProduct(ValidateException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerValidateExceptionProduct: finished with exception: + {}", errMessage);
        return new ResponseDto<>(errMessage, null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<ProductResponseDto> handlerArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerArgumentTypeMismatchException: finished with exception: + {}", errMessage);
        return new ResponseDto<>("ID товара должен быть указан числом! "
                + "Ошибка ввода в: " + ex.getParameter().getParameterName() + ", со значением value: " + ex.getValue(), null);
    }
}


