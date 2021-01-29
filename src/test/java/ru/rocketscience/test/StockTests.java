package ru.rocketscience.test;

import lombok.Data;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rosketscience.test.common.ResponseDto;
import ru.rosketscience.test.stock.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.rosketscience.test.StockTests.TestCase.args;


class StockTests extends BaseApplicationTest {

    /* класс-обёртка, позволяющая для generic-типов сохранить, что внутри, чтобы спринг не забыл о том, какие исходные типы были вложены
    Возвращает тип, представляющий прямой суперкласс сущности (класс, интерфейс, примитивный тип или void), представленный этим классом.
    Если суперкласс является параметризованным типом, то возвращается объект Type должен точно отражать фактические параметры типа,
    используемые в источнике код */
    public static final ParameterizedTypeReference<ResponseDto<StockResponseDto>> STOCK_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    public static final ParameterizedTypeReference<ResponseDto<Long>> LONG_RESPONSE
            = new ParameterizedTypeReference<>() {
    };

    /* Wrapper используем, как альтернативный способ получить вложенный ответ из response;
     *  Альтернативный от ParameterizedTypeReference<<>>
     *  Урезанная копия ResponseDto
     *  Плохо тем, что (частино) копирует ResponseDto */

   /* public static class StockDtoWrapper {
        public StockResponseDto data;
    }

    //использование Wrapper
    StockDtoWrapper wrapper = testRestTemplate.getForObject(resourceUrlId, StockDtoWrapper.class);
    StockResponseDto data = wrapper.data; */

    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(StockTests.class, jsonFileName, dtoClass);
    }

    //делаем переменную статиком и пишем туда результат выполнения метода setupUrl
   // public static String resourceUrl;

    // public static String resourceUrlFilter = resourceUrl + "filterStock/";

    //переменную с jsonReq и Resp выносим статиком чтобы в каждом методе не создавать лишний раз
    private static final String jsonFileNameReq = "/stock/addStock.req.json";

    private static final long stockId = 1L;

    // @BeforeEach - до каждого метода
    @BeforeEach
    //создаём повторяющуюся переменную до старта каждого теста.
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port + "/stock/";
    }

    /* Для чего выносить код в отдельные методы:
    1. Избежание дублируемого кода:
    1.1 Когда код повторяется;
    1.2 Когда несколько строчек кода выполняют определённую задачу(см. tesGet()). Нпрмр: если метод не влазит на 1/2 экрана,
    то надо разделять:).
    */

    //тестирование get-метода положительный сценарий
    @Test
    void testSimpleGet() {
        String jsonFileNameResp = "/stock/addStock.resp.json";
        testGet("2", getStockResponseDto(jsonFileNameResp));
    }

    /* 1. тестирование get-метода отрицательный сценарий, если склада не существует (введен не тот id)
     * 2. тестирование get-метода отрицательный сценарий, если id склада указан String*/

    @ParameterizedTest // краткая запись для нескольких тестов на один и тот же функционал с разными параметрами
    @CsvSource(delimiter = '|', value = { //value - наборы параметров, delimiter - разделитель
            "44|Склада с таким id: 44 не существует",
            "четыре|ID склада должен быть указан числом! Ошибка ввода в: id, со значением value: четыре"})
        //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {
        //формирует ответ сервера (выполнение метода /get при неправильном id)
        //exchange возвращает response!!, Тот метод, который указали в аргументах: HTTP, тот и будет выпоняться
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(stockUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    /*  тестирование add-метода положительный сценарий
     При добавлении проще и правильнее проверять по id добавленного объекта */
    @Test
    void testAdd() {
        /* Создаём Entity, проверяем ее(тесты на null + get-тесты) и берём ID.
         ЗЫ см. CREATE_STOCK + createAndTestStock(); */
        createAndTestStock(jsonFileNameReq);
    }

    //тест delete-метода
    @Test
    void testDelete() {
         /* Создаём Entity, проверяем ее(тесты на null + get-тесты) и берём ID.
         ЗЫ см. CREATE_STOCK + createAndTestStock(); */
        Long id = createAndTestStock(jsonFileNameReq);
        //выполнение метода /del Void.class - тк метод контроллера void
        testRestTemplate.exchange(stockUrl + id, HttpMethod.DELETE, null, Void.class);
        //exchange возвращает response!!, Тот метод, который указали в аргументах. В данном случае -Void - пустоту
               //проверка на выполнение метода delete()
        testInvalidGet(String.valueOf(id), "Склада с таким id: " + id + " не существует");
    }

    //тест update-метода
    @Test
    void testUpdate() {

        String jsonFileNameAfterUpd = "/stock/updateStock.resp.json";
         /* Создаём Entity, проверяем ее(тесты на null + get-тесты) и берём ID.
         ЗЫ см. CREATE_STOCK + createAndTestStock(); */
        Long id = createAndTestStock("/stock/updateStock.req.json");
        //создаем DTO новой сущностью и подставляем значения из преобразованного json: StockToUpdate.json
        StockRequestDto stockRequestDto = getStockRequestDto(jsonFileNameAfterUpd);
        //формирует Http-запрос на сервер для получения данных об Entity
        //создаем requestEntity + метод (post), а так, же указываем URI
        RequestEntity<StockRequestDto> requestEntityUpd =  // body(stockRequestDto) - берём сущность по полученному id.
                RequestEntity.put(URI.create(stockUrl + id)).contentType(MediaType.APPLICATION_JSON)
                        .body(stockRequestDto);

        //выполнение метода /put и ответ от сервера
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntityUpd, Void.class);
        //проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        //выполнение теста get() чтобы проверить, берётся Entity с новыми данными
        testGet(String.valueOf(id), getStockResponseDto(jsonFileNameAfterUpd));
    }

    //список всех складов по имени города
    @Test
    void testGetAllStocksByCityName() {
        String jsonFileNameResp = "/stock/getStocksByCityName.resp.json";

        ParameterizedTypeReference<ResponseDto<StockListResponseDto>> parameterizedTypeReferenceResponse =
                new ParameterizedTypeReference<>() {
                };
       StockListResponseDto stockResponseDto
                = getFromJson(jsonFileNameResp, StockListResponseDto.class);
        ResponseEntity<ResponseDto<StockListResponseDto>> responseEntity
                = testRestTemplate.exchange(
                stockUrl + "stockListByCityName/" + getStockRequestDto("/stock/getStocksByCityName.req.json").getCity(),
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        StockListResponseDto data = responseEntity.getBody().getData();
        assertThat(data.getStockList()).isEqualTo(stockResponseDto.getStockList());
    }

    //Общее количество свободных мест на складе
    @Test
    void testGetMaxCapacityInStock() {
        ResponseEntity<ResponseDto<Long>> responseEntity
                = testRestTemplate.exchange(stockUrl + "maxCapacityInStock/" + 2L,
                HttpMethod.GET, null, LONG_RESPONSE);
        Long data = responseEntity.getBody().getData();
        Long stockFreeSpace = 20L;
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(stockFreeSpace);
    }

    //поиск мест по ид склада вывод в map х : у
    //добавить порядковый номер полочки с выводом в вывод
    // добавить ряд полочки
    @Test
    void testGetStockPlacesFreeSpaceByStockId() {
       StockFreeSpaceInMapDto getFromJson
                = getFromJson("/stock/getStockPlacesFreeSpaceByStockId.resp.json", StockFreeSpaceInMapDto.class);
        ParameterizedTypeReference<ResponseDto<StockFreeSpaceInMapDto>> stockResponse =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ResponseDto<StockFreeSpaceInMapDto>> responseEntity
                = testRestTemplate.exchange(stockUrl + "stockPlacesFreeSpaceByStockId/" + stockRequestDto.getStockId(),
                HttpMethod.GET, null, parameterizedTypeReferenceResponse);
        StockFreeSpaceInMapDto data = responseEntity.getBody().getData();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        StockFreeSpaceInMapDto data = responseEntity.getBody().getData();
        assertThat(data.getStockPlaceIdFreeSpaceByStockId()).isEqualTo(getFromJson.getStockPlaceIdFreeSpaceByStockId());
    }

    //вывод списка всех складских мест по id склада
    @Test
    void testGetAllStockPlacesByStockId() {
        StockResponseDto stockResponseDto = getStockResponseDto("/stock/getStockPlacesByStockId.resp.json");

        ResponseEntity<ResponseDto<StockResponseDto>> responseEntity
                = testRestTemplate.exchange(stockUrl + "allByStockId/" + stockRequestDto.getStockId(),
                HttpMethod.GET, null, STOCK_RESPONSE);

        StockResponseDto data = responseEntity.getBody().getData();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        StockResponseDto data = responseEntity.getBody().getData();
        assertThat(data.getStockPlaceList()).isEqualTo(stockResponseDto.getStockPlaceList());
    }

    /*     values ('searching_Московский склад'(name), 'searching_Москва_city'(city)),
       ('searching_Спб склад', 'searching_Санкт-Марино_city'),
       ('searching_Адмиралтейский склад', 'searching_Санкт-Петербург_city');*/
      // для нескольких тестов на один и тот же функционал с разными параметрами
    @ParameterizedTest
    @MethodSource("generateCases")
    //Если не делаем приведение в стриме, то в аргументах подаём объект и в тесте его разворачиваем
    void filterTestTemplate(TestCase arg) {
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
        stockList.forEach(arg.verifier);
    }

    /*     values ('searching_Московский'(name), 'searching_Москва_city'(city)),
('searching_Спб склад', 'searching_Санкт-Марино_city'),
('searching_Адмиралтейский склад', 'searching_Санкт-Петербург_city');*/
    //requestDto не нужна, тк в URI сразу подаём нужные значения для поиска: name, city и тд.: "?name="
    //?name=склад&city=анкт
    //тк GET запрос, то в exchange: см. пример:
    @Test
    void filterStockByNameAndCity() {
        String resourceUrlFilter = resourceUrl + "searchStock";

        ParameterizedTypeReference<ResponseDto<StockFilterResponseDto>> parameterizedStockResponse =
                new ParameterizedTypeReference<>() {
                };
        //URLEncoder.encode кодируем рус поисковые слова к стандартной UTF-8 для поисковой строки
        // Лучше не дто ФОРМИРОВАТЬ,а отправить сразу в URI
             ResponseEntity<ResponseDto<StockFilterResponseDto>> responseEntityDto
                = testRestTemplate.exchange(URI.create(resourceUrlFilter + "?name="
                        + getEncoded("кла")
                        + "&city=" + getEncoded("анкт")),
                HttpMethod.GET,
                null,
                parameterizedStockResponse);
        assertThat(responseEntityDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityDto.getBody()).isNotNull();

        List<StockResponseDto> stockList = responseEntityDto.getBody().getData().getStockList();
        long count = stockList.stream()
                .filter(sl -> sl.getName().startsWith("searching") && sl.getCity().startsWith("searching"))
                .count();
        assertThat(count).isEqualTo(2);

        stockList.forEach(stockResponseDto ->
                assertThat((stockResponseDto.getName()).contains("кла")
                        && (stockResponseDto.getCity()).contains("анкт")));
    }

    private static String getEncoded(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }
/*
    //requestDto не нужна, тк в URI сразу подаём нужные значения для поиска: name, city и тд.: "?name="
    //?name=склад&city=анкт
    //тк GET запрос, то в exchange: см. пример:
    private void filterTestTemplate2(String expectedName, String expectedPart,
                                     int expectedSize) {
        String resourceUrlFilter = resourceUrl + "filterStock";

        ParameterizedTypeReference<ResponseDto<StockFilterResponseDto>> stockResponse =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ResponseDto<StockFilterResponseDto>> responseEntityDto
                = testRestTemplate.exchange(
                //URLEncoder.encode кодируем рус к стандартной UTF-8 для поисковой строки
                URI.create(resourceUrlFilter + "?name=" + getEncoded(expectedPart)),
                HttpMethod.GET,
                null,
                stockResponse);

        assertThat(responseEntityDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityDto.getBody()).isNotNull(); //responseEntity всегда не null, проверяем body!

        List<StockResponseDto> stockList = responseEntityDto.getBody().getData().getStockList();
        long count = stockList.stream()
                .filter(sl -> sl.getName().startsWith(expectedName)) //что вообще нашёл
                .count();
        assertThat(count).isEqualTo(expectedSize);

        stockList.forEach(stockResponseDto ->
                assertThat(stockResponseDto.getName().contains(expectedPart))); //что соответствует нашему запросу

    }*/

    //если прилетит ошибка, то не сможем ее распарсить, тк тут нет поля error, как в ResponseDto
    //на каждый Wrapper необходимо будет создавать поля с error и писать кучу разного дублирующего друг друга кода
    @Data
    static class Wrapper {
        private StockResponseDto data;
    }

    //выполняет get-запрос и проверку ожидаемого и запрашиваемого
    void testGet(String id, StockResponseDto stockResponseDto) {
        /* подставляем id(взятый из новосозданной сущности) в url и сверяем с тем, что получилось
         * Вместо Wrapper. Формируем ответ сервера (выполнение метода /get) */
        ResponseEntity<ResponseDto<StockResponseDto>> responseEntity
                = testRestTemplate.exchange(stockUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);

        ResponseEntity<Wrapper> responseEntity
                = testRestTemplate.exchange(URI.create(resourceUrl + id), HttpMethod.GET, null, Wrapper.class);
        assertThat(responseEntity.getBody()).isNotNull();
        StockResponseDto data = responseEntity.getBody().getData();

        //всякме тесты на соответствие и тд
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(data.getName()).isEqualTo(stockResponseDto.getName());
        assertThat(data.getCity()).isEqualTo(stockResponseDto.getCity());
    }

    //Создаёт entity и получаем id entity из бд
    private Long createStock(StockRequestDto stockRequestDto) {
        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<StockRequestDto> requestEntity =
                RequestEntity.post(URI.create(stockUrl)).contentType(MediaType.APPLICATION_JSON).body(stockRequestDto);//в body пихаем dto
        //получаем только id из бд, чтобы не тащить все данные оттуда (в контроллере надо вернуть значение id после записи в бд)
        Long id = testRestTemplate.postForObject(stockUrl, requestEntity, Long.class);
        //проверка на null
        assertThat(id).isNotNull();
        return id;
    }

    //метод для получения id сущности при создании из JSON + тесты на null + get-тесты
    private Long createAndTestStock(String jsonFileName) {
        /* создаем и подставляем значения в RequestDto из преобразованного json: addNewProduct.req.json
         * подставляем значения из преобразованного json: addNewProduct.req.json
         * вытаскиваем ID из созданной сущности */
        Long id = createStock(getStockRequestDto(jsonFileName));
        //подставляем значения из преобразованного json: addNewProduct.req.json
        // и тестируем на то, что все записалось
        testGet(String.valueOf(id), getStockResponseDto(jsonFileName));
        return id;
    }

    //создание RequestDto из вх. json'а
    private StockRequestDto getStockRequestDto(String jsonFileNameReq) {
        return getFromJson(jsonFileNameReq, StockRequestDto.class);
    }

    //создание ResponseDto из вх. json'а
    private StockResponseDto getStockResponseDto(String jsonFileNameResp) {
        return getFromJson(jsonFileNameResp, StockResponseDto.class);
    }
//создаем класс с полями, необходимыми для теста и там пишем метод, который будет подставлять аргументы в тест
    @Value
    static class TestCase {
        String searchString;
        Consumer<StockResponseDto> verifier;
        long expectedSize;

        static TestCase args(String searchString, Consumer<StockResponseDto> verifier, long expectedSize) {
            return new TestCase(searchString, verifier, expectedSize);
        }
    }

    //Изначально можно использовать Stream<Arguments>, но надо приводить к
    // args("name=кла",  (Consumer<StockResponseDto> sp -> assertThat(sp.getName()).contains("кла")), 2L);
    private static Stream<TestCase> generateCases() {
        return Stream.of(
                args("name=кла", sp -> assertThat(sp.getName()).contains("кла"), 2L),
                args("city=анкт", sp -> assertThat(sp.getCity()).contains("анкт"), 2L)
        );
    }
    //в реальности в базовом классе реализацию метода, подставляем только path
    //изначально, закладываясь на много абстракций - долго и плохо.
  /*  @Value
    static class TestCase<T> {
        String searchString;
        Consumer<T> verifier;
        long expectedSize;

        static <T> TestCase<T> args(String searchString, Consumer<T> verifier, long expectedSize) {
            return new TestCase<T>(searchString, verifier, expectedSize);
        }
    }
    private static Stream<TestCase<StockResponseDto>> generateCases() {
        return Stream.of(
                args("name=кла", sp -> assertThat(sp.getName()).contains("кла"), 2L),
                args("city=анкт", sp -> assertThat(sp.getCity()).contains("анкт"), 2L)
        );
    }*/

}





