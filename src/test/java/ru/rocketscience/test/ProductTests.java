package ru.rocketscience.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rocketscience.test.product.ProductController;
import ru.rocketscience.test.product.ProductResponseDto;
import ru.rocketscience.test.common.ResponseDto;
import ru.rocketscience.test.product.ProductRequestDto;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTests extends BaseApplicationTest {

    public static final ParameterizedTypeReference<ResponseDto<ProductResponseDto>> PRODUCT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    //создаем и подставляем значения в RequestDto из преобразованного json: NewProduct.json
/*
    public  final ProductRequestDto CREATE_PRODUCT
            = getFromJson("/product/NewProduct.json", ProductRequestDto.class);
*/

    //метод для простоты вызова метода getObjectFromResourceJson();
    private  <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
       return getObjectFromResourceJson(ProductController.class, jsonFileName, dtoClass);
    }


    @Test
    void testSimpleGet() {

        testGet("2", "Товар 2", BigDecimal.valueOf(200));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "47|Товара с id = 47 не существует!",
            "пять|ID товара должен быть указан числом! Ошибка ввода в: id, со значением value: пять"})

        //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null,
                PRODUCT_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    //тест метода /add
    @Test
    void testAdd() {
        ProductRequestDto CREATE_PRODUCT
                = getFromJson("/product/NewProduct.json", ProductRequestDto.class);

        //подставляем значения из преобразованного json: NewProduct.json
        Long id = createProduct(CREATE_PRODUCT.getName(),
                CREATE_PRODUCT.getPrice());

        //подставляем значения из преобразованного json: NewProduct.json
        testGet(String.valueOf(id),
                CREATE_PRODUCT.getName(),
                CREATE_PRODUCT.getPrice());
    }

    //тест delete-метода
    @Test
    void testDelete() {
        ProductRequestDto CREATE_PRODUCT
                = getFromJson("/product/NewProduct.json", ProductRequestDto.class);

        Long productId = createProduct(
                CREATE_PRODUCT.getName(),
                CREATE_PRODUCT.getPrice());

        //выполнение метода /del Void.class - тк метод контроллера void
        testRestTemplate.exchange(resourceUrl + productId, HttpMethod.DELETE, null, Void.class);

        testInvalidGet(String.valueOf(productId), "Товара с id = " + productId + " не существует!");
    }

    //тест update-метода
    @Test
    void testUpdate() {
        ProductRequestDto createProduct
                = getFromJson("/product/NewProduct.json", ProductRequestDto.class);

        Long productId = createProduct(
                createProduct.getName(),
                createProduct.getPrice());

        testGet(String.valueOf(productId),
                createProduct.getName(),
                createProduct.getPrice());

        //создаем DTO новой сущностью и подставляем значения из преобразованного json: ProductToUpdate.json
        ProductRequestDto createProductUpd
                = getFromJson("/product/ProductToUpdate.json", ProductRequestDto.class);

        RequestEntity<ProductRequestDto> requestEntityUpd
                = RequestEntity.put(URI.create(resourceUrl + productId)).contentType(MediaType.APPLICATION_JSON).
                body(createProductUpd);

        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(requestEntityUpd, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        testGet(String.valueOf(productId),
                createProductUpd.getName(),
                createProductUpd.getPrice());
    }

    //обезличенный get-test
    void testGet(String id, String name, BigDecimal price) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
        ProductResponseDto data = response.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(name);
        assertThat(data.getPrice()).isEqualTo(price);
    }

    //Тестовый объект для записи
    private ProductRequestDto createProductRequestDto(String name, BigDecimal price) {
        return ProductRequestDto.builder()
                .name(name)
                .price(price)
                .build();
    }

    //получение id свежезаписанной entity в бд
    private Long createProduct(String name, BigDecimal price) {
        ProductRequestDto productRequest = createProductRequestDto(name, price);

        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<ProductRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(productRequest);

        /* получаем только id из бд, чтобы не тащить все данные оттуда
        (в контроллере надо вернуть значение id после записи в бд)*/
        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);

        assertThat(id).isNotNull();
        return id;
    }
}
