package ru.rocketscience.test;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rocketscience.test.common.ResponseDto;
import ru.rocketscience.test.product.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.rocketscience.test.ProductTests.TestCase.args;
import static ru.rocketscience.test.ProductTests.TestCase.argsCriteria;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductTests extends BaseApplicationTest {

    private static final ParameterizedTypeReference<ResponseDto<ProductResponseDto>> PRODUCT_RESPONSE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<ResponseDto<List<FilterResultDto>>> CRITERIA_PARAMETRIZED = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<ResponseDto<Long>> PARAMETERIZED_RESPONSE_LONG = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<ResponseDto<ProductFilterResponseDto>> PARAMETERIZED_RESPONSE_FILTER = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<ResponseDto<ProductMovementResponseDto>> PARAMETERIZED_RESPONSE_MOVEMENT = new ParameterizedTypeReference<>() {
    };

    //метод для простоты вызова метода getObjectFromResourceJson();
    //конструктор пустого класса возвращает обычные DTO
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(ProductTests.class, jsonFileName, new TypeReference<T>() {
            @Override
            public Type getType() {
                return dtoClass;
            }
        });
    }

    private <T> T getFromJson(String jsonFileName, TypeReference<T> typeReference) {
        return getObjectFromResourceJson(ProductTests.class, jsonFileName, typeReference);
    }

    //метод для создания коллекций из ДТО
    private <T> List<T> getListFromJson(String jsonFileName, Class<? extends Collection> collectionType, Class<T> dtoClass) {
        return getObjectListFromResourceJson(ProductTests.class, collectionType, jsonFileName, dtoClass);
    }

    private static final String jsonFileNameReq = "/product/addNewProduct.req.json";
    private static final String jsonFileNameResp = "/product/addNewProduct.resp.json";

    @BeforeEach
    public void setupUrl() {
        productUrl = "http://localhost:" + port + "/product/";
        stockUrl = "http://localhost:" + port + "/stock/";
    }

    @Test
    void testSimpleGet() {
        testGet("2", getProductResponseDto(jsonFileNameResp));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "47|Товара с id = 47 не существует!",
            "пять|Значение должно быть указано числом! Ошибка ввода в: id, со значением value: пять"})

        //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {

        //создаем и подставляем значения в RequestDto из преобразованного json: addNewProduct.req.json
        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(productUrl + id, HttpMethod.GET, null,
                PRODUCT_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
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

        ResponseEntity<ResponseDto<Long>> responseEntityBeforeAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, PARAMETERIZED_RESPONSE_LONG);
        assertThat(responseEntityBeforeAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityBeforeAddingProduct.getBody()).isNotNull();

        RequestEntity<ProductPlacementDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlAddProducts)).contentType(MediaType.APPLICATION_JSON)
                .body(productPlacementDto);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntity, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ResponseDto<Long>> responseEntityAfterAddingProduct
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, PARAMETERIZED_RESPONSE_LONG);
        ResponseDto<Long> responseEntityAfter = responseEntityAfterAddingProduct.getBody();
        assertThat(responseEntityAfterAddingProduct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityAfter).isNotNull();
        assertThat(responseEntityAfter.getData()).isEqualTo(stockFreeSpace);
        assertThat(responseEntityAfter.getData()).isEqualTo(stockFreeSpace);
    }

    //фильтр по названию товара
 /*   @ParameterizedTest
    @MethodSource("generateCases")
    void searchProduct(TestCase<ProductResponseDto> args) {

        String resourceUrlFilter = productUrl + "filterProduct";

        ResponseEntity<ResponseDto<ProductFilterResponseDto>> responseEntity
                = testRestTemplate.exchange(
                resourceUrlFilter + "?" + args.searchString,
                HttpMethod.GET,
                null,
                PARAMETERIZED_RESPONSE_FILTER);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<ProductResponseDto> productFilterList = responseEntity.getBody().getData().getProductList();
        long count = productFilterList.stream()
                .filter(pl -> pl.getName().startsWith("searching")).count();

        assertThat(count).isEqualTo(args.expectedSize);
        productFilterList.forEach(args.verifier);
    }*/

    @ParameterizedTest
    @MethodSource("casesForCriteria")
        //
    void searchingByCityAndProduct(TestCase<List<FilterResultDto>> args) {
        String productFilterUrl = productUrl + "filter";

        ResponseEntity<ResponseDto<List<FilterResultDto>>> responseEntity
                = testRestTemplate.exchange(productFilterUrl + "?" + args.searchString,
                HttpMethod.GET,
                null,
                CRITERIA_PARAMETRIZED);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<FilterResultDto> dataList = responseEntity.getBody().getData();
        long count = dataList.stream()
                .filter(pl -> pl.getProduct().getName().startsWith("search2")).count();

        assertThat(count).isEqualTo(args.getExpectedSize());
        args.verifier.accept(dataList); //сравнение списков DTO: response + from Stream
    }

    /*@Test
    void searchingByCityAndProduct() {
        String productFilterUrl = productUrl + "filter";
        Long expectedSize = 2L;
        ResponseEntity<ResponseDto<List<FilterResultDto>>> responseEntity
                = testRestTemplate.exchange(productFilterUrl + "?product=uct",
                HttpMethod.GET,
                null,
                CRITERIA_PARAMETRIZED);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<FilterResultDto> dataList = responseEntity.getBody().getData();
        long count = dataList.stream()
                .filter(pl -> pl.getProduct().getName().startsWith("search2")).count();

        assertThat(count).isEqualTo(expectedSize);

        List<FilterResultDto> listFromJson = getFromJson("/product/criteriaSearchByName.resp.json",
                new TypeReference<>() {
                });
        dataList.forEach(dt ->
                assertThat(dt.getProduct().getName())
                        .contains("uct"));
        assertThat(dataList).isEqualTo(listFromJson);
    }*/

    //перемещение товаров между складами
    @Test
    void movementProductsBetweenStocks() {
        String resourceUrlForStock = "http://localhost:" + port + "/stock/maxCapacityInStock/";
        long stockId = 6L;
        long stockFreeSpace = 502L;

        String resourceUrlMovementProducts = productUrl + "moveProducts/";
        ProductMovementRequestDto movementRequestDto
                = getFromJson("/product/movementProductsBetweenStocks.req.json", ProductMovementRequestDto.class);

        ProductMovementResponseDto movementResponseDtoJson
                = getFromJson("/product/movementProductsBetweenStocks.resp.json", ProductMovementResponseDto.class);

        RequestEntity<ProductMovementRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlMovementProducts)).
                contentType(MediaType.APPLICATION_JSON).body(movementRequestDto);

        ResponseEntity<ResponseDto<ProductMovementResponseDto>> responseEntity
                = testRestTemplate.exchange(requestEntity, PARAMETERIZED_RESPONSE_MOVEMENT);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        ProductMovementResponseDto data = responseEntity.getBody().getData();
        assertThat(data.getProductId()).isEqualTo(movementResponseDtoJson.getProductId());
        assertThat(data.getStockplaceId()).isEqualTo(movementResponseDtoJson.getStockplaceId());
        assertThat(data.getStockId()).isEqualTo(movementResponseDtoJson.getStockId());

        ResponseEntity<ResponseDto<Long>> responseEntityAfterMovingProducts
                = testRestTemplate.exchange(resourceUrlForStock + stockId,
                HttpMethod.GET, null, PARAMETERIZED_RESPONSE_LONG);
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

    //метод для кодировок в
    private String getEncoded(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @Value //класс тесткейсов. В параметрах поисковая строка + Consumer, а так ж ожидаемый размер длины списка
    static class TestCase<T> {
        String searchString;
        Consumer<T> verifier;
        long expectedSize;

        static TestCase<ProductResponseDto> args(String searchString, Consumer<ProductResponseDto> verifier, long expectedSize) {
            return new TestCase<>(searchString, verifier, expectedSize);
        }

        static TestCase<List<FilterResultDto>> argsCriteria(
                String searchString, Consumer<List<FilterResultDto>> verifier, long expectedSize) {
            return new TestCase<>(searchString, verifier, expectedSize);
        }
    }

    //создать имя+минцена+максцена в args
    private static Stream<TestCase<ProductResponseDto>> generateCases() {
        return Stream.of(
                args("name=неизв", pr -> assertThat(pr.getName()).contains("неизв"), 2L),
                args("minPrice=510", pr -> assertThat(pr.getPrice()).isGreaterThan(BigDecimal.valueOf(510)), 3L),
                args("maxPrice=590", pr -> assertThat(pr.getPrice()).isLessThan(BigDecimal.valueOf(590)), 1L),
                args("name=извест&minPrice=510&maxPrice=770",
                        pr -> {
                            assertThat(pr.getName()).contains("извест");
                            assertThat(pr.getPrice()).isGreaterThan(BigDecimal.valueOf(510));
                            assertThat(pr.getPrice()).isLessThan(BigDecimal.valueOf(710));
                        },
                        2L));
    }

    private Stream<TestCase<List<FilterResultDto>>> casesForCriteria() {
        List<FilterResultDto> listFilterByName
                = getFromJson("/product/criteriaSearchByName.resp.json", new TypeReference<>() {
        });
        List<FilterResultDto> listFilterByCity
                = getFromJson("/product/criteriaSearchByCity.resp.json", new TypeReference<>() {
        });
        List<FilterResultDto> listFilterByNameCity
                = getFromJson("/product/criteriaSearchByNameCity.resp.json", new TypeReference<>() {
        });
        return Stream.of(
               /* argsCriteria("product=uct", result -> {
                    result.forEach(each -> assertThat(each.getProduct().getName()).contains("uct"));
                    assertThat(result).isEqualTo(listFilterByName);
                }, 2L),

                argsCriteria("city=ch2_city", result -> {
                    assertThat(result.get(0).getStockDto().get(0).getCity()).contains("ch2_city");
                    assertThat(result.get(1).getStockDto().get(0).getCity()).contains("ch2_city");
                    assertThat(result.get(2).getStockDto().get(0).getCity()).contains("ch2_city");
                    assertThat(result).isEqualTo(listFilterByCity);
                }, 3L),*/
              /*  argsCriteria("product=rod_3&city=ch2_city", result -> {
                    assertThat(result.get(0).getProduct().getName()).contains("rod_3");
                    assertThat(result.get(0).getStockDto().get(0).getCity()).contains("ch2_city");
                    assertThat(result.get(0).getStockDto().get(0).getQuantity()).isEqualTo(30);
                    assertThat(result).isEqualTo(listFilterByNameCity);
                }, 1L),*/
                argsCriteria("product=uct_1&city=search2_city_2", result -> {
                    assertThat(result.get(0).getProduct().getName()).contains("uct_1");
                    assertThat(result.get(0).getStockDto().get(0).getId()).isEqualTo(11);
                    assertThat(result.get(0).getStockDto().get(0).getQuantity()).isEqualTo(30);
                    assertThat(result.get(1).getProduct().getName()).contains("uct_1");
                    assertThat(result.get(1).getStockDto().get(0).getId()).isEqualTo(11);
                    assertThat(result.get(1).getStockDto().get(0).getQuantity()).isEqualTo(30);
                    assertThat(result.get(1).getProduct().getName()).contains("uct_1");
                    assertThat(result.get(1).getStockDto().get(1).getId()).isEqualTo(12);
                    assertThat(result.get(1).getStockDto().get(1).getQuantity()).isEqualTo(30);

                }, 3L)
        );
    }
}
/*    INSERT INTO stock (name, city)
    values ('search3_склад_1', 'search3_город_1'), *//*id = 11*//*
       ('search3_склад_2', 'search3_город_1'), *//*id = 12*//*
               ('search3_склад_3', 'search3_город_2'); *//*id = 13*//*

               INSERT INTO product(name, price)
               values ('search3_товар_1', 1100), *//*id = 12*//*
               ('search3_товар_2', 1200), *//*id = 13*//*
               ('search3_тов_3', 1300); *//*id = 14*//*

               INSERT INTO stock_place (row, shelf, capacity, stock_id)
               values ('search3_Первый_ряд', 1, 50, 11), *//*sp_id = 14*//*
               ('search3_Первый_ряд', 2, 60, 11), *//*sp_id = 15*//*
               ('search3_Второй_ряд', 2, 70, 12); *//*sp_id = 16*//*

               INSERT INTO product_on_stock_place(product_id, stock_place_id, quantity_product)
               values (12, 14, 10),
               (13, 15, 20),
               (14, 16, 30);*/
