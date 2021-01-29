package ru.rocketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rosketscience.test.common.ResponseDto;
import ru.rosketscience.test.product.ProductPlacementDto;
import ru.rosketscience.test.product.ProductRequestDto;
import ru.rosketscience.test.product.ProductResponseDto;
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

 /*   @BeforeEach
    public void setupUrl() {
        productUrl = resourceUrl + "/product/";

    }*/

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
                = testRestTemplate.exchange(productUrl + id, HttpMethod.GET, null,
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
        testRestTemplate.exchange(productUrl + id, HttpMethod.DELETE, null, Void.class);

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
                = RequestEntity.put(URI.create(productUrl + id)).contentType(MediaType.APPLICATION_JSON).
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
        String resourceUrlForStock = stockUrl + "/maxCapacityInStock/";
        String resourceUrlAddProducts = productUrl + "addProducts/";
        long stockId = 1L;
        Long stockFreeSpace = 21L;

        ProductPlacementDto productPlacementDto
                = getFromJson("/product/addManyNewProducts.req.json", ProductPlacementDto.class);

        ParameterizedTypeReference<ResponseDto<Long>> parameterizedTypeReferenceResponse =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ResponseDto<Long>> responseEntityBeforeAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        assertThat(responseEntityBeforeAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityBeforeAddingProduct.getBody()).isNotNull();

        RequestEntity<ProductPlacementDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlAddProducts)).contentType(MediaType.APPLICATION_JSON)
                .body(productPlacementDto);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntity, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ResponseDto<Long>> responseEntityAfterAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        ResponseDto<Long> responseEntityAfter = responseEntityAfterAddingProduct.getBody();
        assertThat(responseEntityAfterAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityAfter).isNotNull();
        assertThat(responseEntityAfter.getData()).isEqualTo(stockFreeSpace);
        assertThat(responseEntityAfter.getData()).isEqualTo(stockFreeSpace);
    }

    //фильтр по названию товара
    @ParameterizedTest
    @MethodSource("generateCases")
    void searchProductByName(TestCase<ProductResponseDto> args) {

        String resourceUrlFilter = resourceUrl + "filterProduct";

        ParameterizedTypeReference<ResponseDto<ProductFilterResponseDto>> parameterizedResponse
                = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ResponseDto<ProductFilterResponseDto>> responseEntity
                = testRestTemplate.exchange(
                resourceUrlFilter + "?" + args.searchString,
                HttpMethod.GET,
                null,
                parameterizedResponse);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<ProductResponseDto> productFilterList = responseEntity.getBody().getData().getProductList();
        long count = productFilterList.stream()
                .filter(pl -> pl.getName().startsWith("searching")).count();

        assertThat(count).isEqualTo(args.expectedSize);
        productFilterList.forEach(args.verifier);
    }

    //вывод списка id товаров, id принадлежащего склада, их остатка на складах по имени города и имени товара
    //values ('searching_Московский', 'searching_Москва_city'),
    //       ('searching_Спб склад', 'searching_Санкт-Марино_city'),
    //       ('searching_Адмиралтейский склад', 'searching_Санкт-Петербург_city');
    //
    //INSERT INTO product(name, price)
    //values ('searching_носки', 500),
    //       ('searching_неизвестная хрень', 600),
    //       ('searching_безумно неизвестная хрень', 700),
    //       ('searching_шоколадка', 800);
    @ParameterizedTest
    @MethodSource("casesFoCriteria")
    void searchingByCityAndProduct(TestCase<ProductCriteriaFilterResponseDto> args) {
        String productFilterUrl = resourceUrl + "filterCriteria";

        ParameterizedTypeReference<ResponseDto<List<ProductCriteriaFilterResponseDto>>> parameterizedResponse
                = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<ResponseDto<List<ProductCriteriaFilterResponseDto>>> responseEntity
                = testRestTemplate.exchange(productFilterUrl + "?" + args.searchString,
                HttpMethod.GET,
                null,
                parameterizedResponse);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<ProductCriteriaFilterResponseDto> dataList = responseEntity.getBody().getData();
        long count = dataList.stream()
                .filter(pl -> pl.getProductName().startsWith("searching")).count();

        assertThat(count).isEqualTo(args.getExpectedSize());
        dataList.forEach(args.getVerifier());
    }


 /*   void filterTestTemplate(TestCase arg) {
        String resourceUrlFilter = resourceUrl + "searchStock";
        ParameterizedTypeReference<ResponseDto<StockFilterResponseDto>> stockResponse =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ResponseDto<StockFilterResponseDto>> responseEntityDto
                = testRestTemplate.exchange(
                //URLEncoder.encode кодируем рус к стандартной UTF-8 для поисковой строки
                resourceUrlFilter + "?" + arg.searchString,
                HttpMethod.GET,
                null,
                stockResponse);
        assertThat(responseEntityDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityDto.getBody()).isNotNull(); //responseEntity всегда не null, проверяем body!

        List<StockResponseDto> stockList = responseEntityDto.getBody().getData().getStockList();

        long count = stockList.stream()
                .filter(sl -> sl.getName().startsWith("searching"))
                .count();
        assertThat(count).isEqualTo(arg.expectedSize);
        stockList.forEach(arg.verifier);*/

    /*INSERT INTO product(name, price)
values ('searching_носки', 500),
       ('searching_неизвестная хрень', 600),
       ('searching_безумно неизвестная хрень', 700),
       ('searching_шоколадка', 800);
*/

    //поиск по названию в диапазоне цен
    //Выдаёт неправильные данные, при макс диапазоне.
    @Test
    void searchProductByNameAndPrice() {
        String productFilterUrl = resourceUrl + "filterProduct";

        ParameterizedTypeReference<ResponseDto<ProductFilterResponseDto>> responseDtoParameterized
                = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ResponseDto<ProductFilterResponseDto>> responseEntity
                = testRestTemplate.exchange(productFilterUrl + "?name=неизв&minPrice=510&maxPrice=770",
                HttpMethod.GET,
                null,
                responseDtoParameterized);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<ProductResponseDto> productFilteredList = responseEntity.getBody().getData().getProductList();
        //см тут
        long count = productFilteredList.stream()
                .filter(pr ->
                        pr.getName().startsWith("searching"))
                .count();
        assertThat(count).isEqualTo(3);

        productFilteredList.forEach(pr -> {
            assertThat(pr.getName()).contains("неизв");
            assertThat(pr.getPrice()).isGreaterThan(BigDecimal.valueOf(510));
            assertThat(pr.getPrice()).isLessThan(BigDecimal.valueOf(770));
        });
    }

    //перемещение товаров между складами
    @Test
    void movementProductsBetweenStocks() {
        String resourceUrlForStock = "http://localhost:" + port + "/stock/maxCapacityInStock/";
        long stockId = 6L;
        long stockFreeSpace = 502L;

        String resourceUrlMovementProducts = resourceUrl + "moveProducts/";
        ProductMovementRequestDto productMovementRequestDto
                = getFromJson("/product/movementProductsBetweenStocks.req.json", ProductMovementRequestDto.class);

        ProductMovementResponseDto productMovementResponseDtoJson
                = getFromJson("/product/movementProductsBetweenStocks.resp.json", ProductMovementResponseDto.class);

        ParameterizedTypeReference<ResponseDto<ProductMovementResponseDto>> parameterizedProductResponse
                = new ParameterizedTypeReference<>() {
        };
        ParameterizedTypeReference<ResponseDto<Long>> parameterizedResponseAfterMoving =
                new ParameterizedTypeReference<>() {
                };
        RequestEntity<ProductMovementRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlMovementProducts)).
                contentType(MediaType.APPLICATION_JSON).body(productMovementRequestDto);

        ResponseEntity<ResponseDto<ProductMovementResponseDto>> responseEntity
                = testRestTemplate.exchange(requestEntity, parameterizedProductResponse);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        ProductMovementResponseDto data = responseEntity.getBody().getData();
        assertThat(data.getProductId()).isEqualTo(productMovementResponseDtoJson.getProductId());
        assertThat(data.getStockplaceId()).isEqualTo(productMovementResponseDtoJson.getStockplaceId());
        assertThat(data.getStockId()).isEqualTo(productMovementResponseDtoJson.getStockId());

        ResponseEntity<ResponseDto<Long>> responseEntityAfterMovingProducts
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, parameterizedResponseAfterMoving);
        assertThat(responseEntityAfterMovingProducts.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityAfterMovingProducts).isNotNull();

        assertThat(responseEntityAfterMovingProducts.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityAfterMovingProducts.getBody()).isNotNull();
        assertThat(responseEntityAfterMovingProducts.getBody().getData())
                .isEqualTo(stockFreeSpace);
    }

    //обезличенный get-test
    void testGet(String id, ProductResponseDto productResponseDto) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(productUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        ProductResponseDto data = response.getBody().getData();

        assertThat(data.getName()).isEqualTo(productResponseDto.getName());
        assertThat(data.getPrice()).isEqualTo(productResponseDto.getPrice());
    }

    //СОЗДАНИЕ сущности и получение id свежезаписанной entity в бд
    private Long createProductForStockMethods(ProductRequestDto productRequestDto) {

        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<ProductRequestDto> requestEntity =
                RequestEntity.post(URI.create(productUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(productRequestDto);

        /* получаем только id из бд, чтобы не тащить все данные оттуда
        (в контроллере надо вернуть значение id после записи в бд)*/
        Long id = testRestTemplate.postForObject(productUrl, requestEntity, Long.class);
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

    private String getEncoded(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @Value
    static class TestCase<T> {
        String searchString;
        Consumer<T> verifier;
        long expectedSize;

        static TestCase<ProductResponseDto> args(String searchString, Consumer<ProductResponseDto> verifier, long expectedSize) {
            return new TestCase<>(searchString, verifier, expectedSize);
        }

        static TestCase<ProductCriteriaFilterResponseDto> argsCriteria(
                String searchString, Consumer<ProductCriteriaFilterResponseDto> verifier, long expectedSize) {
            return new TestCase<>(searchString, verifier, expectedSize);
        }
    }
//создать имя+минцена+максцена в аргс
    private static Stream<TestCase<ProductResponseDto>> generateCases() {
        return Stream.of(
                args("name=неизв", pr -> assertThat(pr.getName()).contains("неизв"), 2L),
                args("minPrice=510", pr -> assertThat(pr.getPrice()).isGreaterThan(BigDecimal.valueOf(510)), 3L),
                args("maxPrice=590", pr -> assertThat(pr.getPrice()).isLessThan(BigDecimal.valueOf(590)), 1L)
        );
    }

    private static Stream<TestCase<ProductCriteriaFilterResponseDto>> casesFoCriteria() {
        return Stream.of(
                argsCriteria("product=рен", pr -> assertThat(pr.getProductName()).contains("рен"), 2L),
                argsCriteria("city=оскв", pr -> assertThat(pr.getProductName()).contains("оскв"), 2L)
        );
    }

}
