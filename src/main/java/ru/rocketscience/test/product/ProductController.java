package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.common.ResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Slf4j //включаем логировнаие
@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* @Slf4j = добавляет строчку в класс
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    LoggerFactory - при обращении с методом getLogger, поднимаясь, вычитывает конфигурацию на логи, создаёт иерархию логгеров

    * */

    @GetMapping("{id}")
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

    @PostMapping("addProducts")
    void addProducts(@RequestBody ProductPlacementDto productPlacementDto) {
        log.debug("addProducts: started with data: {}", productPlacementDto);
        productService.addProductsToStockPlace(productPlacementDto);
        log.info("addProducts: finished for data: {}", productPlacementDto);
    }

    //поиск товара по параметрам товара
    @GetMapping("filterProduct")
    ResponseDto<ProductFilterResponseDto> findProductByParam(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice) {
        log.debug("findProductByParam: started with name: {}, minPrice: {}, maxPrice: {}", name, minPrice, maxPrice);
        ProductFilterResponseDto result = productService.filterProductByParam(name, minPrice, maxPrice);
        log.info("findProductByParam: finished with result: {}", result);
        return new ResponseDto<>(null, result);
    }

    //сложный поиск по названию города и товара
    @GetMapping("filter")
    ResponseDto<List<FilterResultDto>> findByCriteria(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "product", required = false) String product) {
        log.debug("findByCriteria: started with city: {}, and product: {}", city, product);
        List<FilterResultDto> result = productService.resultCriteriaFilter(city, product);
        log.info("findByCriteria: finished with result: {}", result);
        return new ResponseDto<>(null, result);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        log.debug("delete: started with id: {}", id);
        productService.delete(id);
        log.info("delete: finished for id: {}", id);
    }

    @PutMapping("{id}")
    void update(@RequestBody ProductRequestDto productRequestDto, @PathVariable Long id) {
        log.debug("update: started with id: + {}", id);
        productService.update(id, productRequestDto);
        log.info("update: finished for id: {}", id);
    }

    @PostMapping("moveProducts")
    ResponseDto<ProductMovementResponseDto> moveProductsBetweenStocks(
            @RequestBody ProductMovementRequestDto productMovementRequestDto) {
        log.debug("moveProductsBetweenStocks: started with data: {}", productMovementRequestDto);
        ProductMovementResponseDto result = productService.movementProductsBetweenStocks(productMovementRequestDto);
        log.info("moveProductsBetweenStocks: finished with data: {}", productMovementRequestDto);
        return new ResponseDto<>(null, result);
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
        return new ResponseDto<>("Значение должно быть указано числом! "
                + "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }
}


