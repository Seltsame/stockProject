package ru.rosketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rosketscience.test.common.ResponseDto;
import ru.rosketscience.test.product.*;
import ru.rosketscience.test.stock.StockFreeSpaceResponseDto;
import ru.rosketscience.test.stock.StockRequestDto;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTests extends BaseApplicationTest {

    public static final ParameterizedTypeReference<ResponseDto<ProductResponseDto>> PRODUCT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };


    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(ProductTests.class, jsonFileName, dtoClass);
    }

    private static final String jsonFileNameReq = "/product/addNewProduct.req.json";
    private static final String jsonFileNameResp = "/product/addNewProduct.resp.json";

    @BeforeEach
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port + "/product/";
    }


    @Test
    void testSimpleGet() {

        testGet("2", getProductResponseDto(jsonFileNameResp));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "47|Товара с id = 47 не существует!",
            "пять|ID товара должен быть указан числом! Ошибка ввода в: id, со значением value: пять"})

        //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {

        //создаем и подставляем значения в RequestDto из преобразованного json: addNewProduct.req.json
        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null,
                PRODUCT_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    //тест метода /add
    @Test
    void testAdd() {

        //создаём, тестируем entity из json-файла и возвращаем id
        createAndTestProductForStockMethods(jsonFileNameReq);
    }

    //тест delete-метода
    @Test
    void testDelete() {

        //ЭТО ВСЁ ЛУЧШЕ ВЫНОСИТЬ В ОТДЕЛЬНЫЙ МЕТОД. См. остальные тесты. З.Ы. просто пример.
        //берем данные из json и пишем в RequestDTO
        ProductRequestDto getFromJson
                = getFromJson(jsonFileNameReq, ProductRequestDto.class);
        //создаём Entity, записываем в базу, сразу же возвращаем её ID
        Long id = createProductForStockMethods(getFromJson);

        //проверка на то, что все хорошо записалось
        testGet(String.valueOf(id), getFromJson(jsonFileNameReq, ProductResponseDto.class));

        //выполнение метода /del Void.class - тк метод контроллера void
        testRestTemplate.exchange(resourceUrl + id, HttpMethod.DELETE, null, Void.class);

        testInvalidGet(String.valueOf(id), "Товара с id = " + id + " не существует!");
    }

    //тест update-метода
    @Test
    void testUpdate() {

        String jsonFileName = "/product/updateProduct.req.json";
        String jsonFileNameUdp = "/product/updateProduct.resp.json";

        Long id = createAndTestProductForStockMethods(jsonFileName);

        //создаем DTO с новыми данными и подставляем значения из преобразованного json: updateProduct.resp.json
        ProductRequestDto createProductUpd
                = getProductRequestDto(jsonFileNameUdp);

        RequestEntity<ProductRequestDto> requestEntityUpd
                = RequestEntity.put(URI.create(resourceUrl + id)).contentType(MediaType.APPLICATION_JSON).
                body(createProductUpd);

        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(requestEntityUpd, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        testGet(String.valueOf(id), getProductResponseDto(jsonFileNameUdp));
    }

    // добавление нескольких продуктов на stockPlace
    /* Чтобы правильно проверить работу метода, мы берем метод из Stock тестов по выводу остатка свободных мест на складе.
     * считаем показатели до и сравниваем с предполагаемыми и показатели после и сравниваем с предполагаемыми(с учётом
     * добавления количества товара)*/
    @Test
    void testAddProductsToStockPlace() {
        String resourceUrlForStock = "http://localhost:" + port + "/stock/maxCapacityInStock/";
        String resourceUrlAddProducts = resourceUrl + "addProducts/";
        StockFreeSpaceResponseDto stockResponseDtoAfter = getFromJson(
                "/product/getMaxCapacityInStockAfterAddingProducts.resp.json", StockFreeSpaceResponseDto.class);
        ProductPlacementDto productPlacementDto
                = getFromJson("/product/addManyNewProducts.req.json", ProductPlacementDto.class);
        StockRequestDto stockRequestDto
                = getFromJson("/stock/getMaxCapacityInStock.req.json", StockRequestDto.class);
        ParameterizedTypeReference<ResponseDto<Long>> parameterizedTypeReferenceResponse =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ResponseDto<Long>> responseEntityBeforeAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockRequestDto.getStockId(),
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        assertThat(responseEntityBeforeAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityBeforeAddingProduct.getBody()).isNotNull();

        RequestEntity<ProductPlacementDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlAddProducts)).contentType(MediaType.APPLICATION_JSON)
                .body(productPlacementDto);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntity, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ResponseDto<Long>> responseEntityAfterAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockRequestDto.getStockId(),
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        ResponseDto<Long> responseEntityAfter = responseEntityAfterAddingProduct.getBody();
        assertThat(responseEntityAfterAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityAfter).isNotNull();
        assertThat(responseEntityAfter.getData()).isEqualTo(stockResponseDtoAfter.getStockFreeSpace());
    }

    @Test
    void movementProductsBetweenStocks() {

        String resourceUrlMovementProducts = resourceUrl + "moveProducts/";
        ProductMovementRequestDto productMovementRequestDto
                = getFromJson("/product/movementProductsBetweenStocks.req.json", ProductMovementRequestDto.class);

        ProductMovementResponseDto productMovementResponseDto
                = getFromJson("/product/movementProductsBetweenStocks.resp.json", ProductMovementResponseDto.class);

        ResponseEntity<ProductMovementResponseDto> responseFromOldStock
                = testRestTemplate.exchange(resourceUrlMovementProducts + productMovementRequestDto.getProductId(), HttpMethod.GET,
                null, ProductMovementResponseDto.class);


        RequestEntity<ProductMovementRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlMovementProducts)).
                contentType(MediaType.APPLICATION_JSON).body(productMovementRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrlMovementProducts, requestEntity, Long.class);
        assertThat(id).isNotNull();


    /*//    String resourceUrlMovementProducts = resourceUrl + "moveProducts/";
       // ProductMovementRequestDto productMovementRequestDto = getFromJson(
             //   "/product/movementProductsBetweenStocks.req.json", ProductMovementRequestDto.class);
        ProductMovementResponseDto productMovementResponseDto
                = getFromJson("/product/movementProductsBetweenStocks.resp.json", ProductMovementResponseDto.class);

        ParameterizedTypeReference<ResponseDto<ProductMovementResponseDto>> parameterizedTypeReferenceResponse =
                new ParameterizedTypeReference<>() {
                };

       // createAndTestProductForStockMethods(jsonFileNameReq);
        ProductRequestDto productRequestDto = getProductRequestDto(jsonFileNameResp);
        RequestEntity<ProductRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(productRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);
        assertThat(id).isNotNull();
       // testGet(String.valueOf(id), getProductResponseDto(jsonFileNameResp));
        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
        ProductResponseDto data = response.getBody().getData();
        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo("Неизвестная хрень");
        assertThat(data.getPrice()).isEqualTo("200");

        ProductRequestDto createProductUpd = ProductRequestDto.builder()
                .name(productRequestDto.getName())
                .price(productRequestDto.getPrice())
                .stockPlaceId(4)
                .build();

        RequestEntity<ProductRequestDto> requestEntityUpd
                = RequestEntity.put(URI.create(resourceUrl + id)).contentType(MediaType.APPLICATION_JSON).
                body(createProductUpd);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntityUpd, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);


        ProductMovementRequestDto productMovementRequestDto = ProductMovementRequestDto.builder()
                .productId(1)
                .productQuantityToMove(1)
                .stockPlaceIdFrom(1)
                .finalStockPlaceId(2)
                .build();


        RequestEntity<ProductMovementRequestDto> bodyRequest = RequestEntity.post(URI.create(resourceUrl + "moveProducts/")).contentType(MediaType.APPLICATION_JSON)
                .body(productMovementRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrl, bodyRequest, Long.class);
        assertThat(id).isNotNull();

        ResponseEntity<ProductMovementResponseDto> getProductResponse
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, ProductMovementResponseDto.class);
        assertThat(getProductResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getProductResponse).isNotNull();*/


//getProductResponse.getBody().

       /* RequestEntity<ProductMovementRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlMovementProducts)).contentType(MediaType.APPLICATION_JSON)
                .body(productMovementRequestDto);
        ResponseEntity<Void> exchange = testRestTemplate.exchange(resourceUrlMovementProducts, HttpMethod.PUT, requestEntity, Void.class);
        //  Long id = testRestTemplate.exchange(resourceUrlMovementProducts, requestEntity, Long.class);
       // ResponseEntity<Long> id = testRestTemplate.exchange(requestEntity, Long.class);
       // testRestTemplate.exchange(resourceUrlMovementProducts, HttpMethod.PUT, requestEntity, Void.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange).isNotNull();

        ResponseEntity<ResponseDto<ProductMovementRequestDto>> responseEntity
                = testRestTemplate.exchange(resourceUrlMovementProducts + productMovementResponseDto., HttpMethod.GET,
                null, parameterizedTypeReferenceResponse);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(id).isNotNull();
        ProductMovementRequestDto data = responseEntity.getBody().getData();
        assertThat(data.getProductId()).isEqualTo(productMovementResponseDto.getProductId());
        assertThat(data.getFinalStockPlaceId()).isEqualTo(productMovementResponseDto.getStockplaceId());*/
    }

    @Test
    void searchingByCityName() {

    }

    //обезличенный get-test
    void testGet(String id, ProductResponseDto productResponseDto) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
        ProductResponseDto data = response.getBody().getData();
        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(productResponseDto.getName());
        assertThat(data.getPrice()).isEqualTo(productResponseDto.getPrice());
    }

    //СОЗДАНИЕ сущности и получение id свежезаписанной entity в бд
    private Long createProductForStockMethods(ProductRequestDto productRequestDto) {

        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<ProductRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(productRequestDto);

        /* получаем только id из бд, чтобы не тащить все данные оттуда
        (в контроллере надо вернуть значение id после записи в бд)*/
        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);
        assertThat(id).isNotNull();
        return id;
    }

    //метод для получения id сущности при создании из JSON + тесты на null + get-тесты
    private Long createAndTestProductForStockMethods(String jsonFileName) {

        //подставляем значения из преобразованного json: addNewProduct.req.json
        //берем id сущности
        Long id = createProductForStockMethods(getProductRequestDto(jsonFileName));

        //подставляем значения из преобразованного json: addNewProduct.req.json
        testGet(String.valueOf(id), getProductResponseDto(jsonFileName));

        return id;
    }

    //создание RequestDto из вх. json'а
    private ProductRequestDto getProductRequestDto(String jsonFileNameReq) {
        return getFromJson(jsonFileNameReq, ProductRequestDto.class);
    }

    //создание ResponseDto из вх. json'а
    private ProductResponseDto getProductResponseDto(String jsonFileNameResp) {
        return getFromJson(jsonFileNameResp, ProductResponseDto.class);
    }
}
