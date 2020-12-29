package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.common.ResponseDto;

@RestController
@RequestMapping(path = "product")
@Slf4j //включаем логировнаие
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(path = "{id}")
    @ResponseBody
    // ResponseDto<StockResponseDto> - через спец DTO (которая разделяется на err и data) пропускаем рабочую DTO
    public ResponseDto<ProductResponseDto> getById(@PathVariable Long id) {
        log.debug("get: started with: {}", id);
        ProductResponseDto result = productService.getById(id);
        log.info("get: finished with: {}, {}", id, result);
        return new ResponseDto<>(null, result); //если ошибки нет, то возвращается result
    }

    @PostMapping
    Long add(@RequestBody ProductRequestDto productRequestDto) {

        return productService.add(productRequestDto);
    }

    @DeleteMapping(path = "{id}")
    void delete(@PathVariable Long id) {
        log.debug("delete: started with: {}", id);
        productService.delete(id);
        log.info("delete: finished with: {}", id);
    }

    @PutMapping(path = "{id}")
    void update(@RequestBody ProductRequestDto productRequestDto, @PathVariable Long id) {
        log.debug("update: starter with: + {}", id);
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


