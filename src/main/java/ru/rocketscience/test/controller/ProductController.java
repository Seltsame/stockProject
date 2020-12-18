package ru.rocketscience.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.dto.ResponseDto;
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


