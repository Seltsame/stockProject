package ru.rosketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rosketscience.test.common.ResponseDto;
import ru.rosketscience.test.stock.StockResponseDto;
import ru.rosketscience.test.stock.StockRequestDto;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class StockTests extends BaseApplicationTest {

    /* класс-обёртка, позволяющая для generic-типов сохранить, что внутри, чтобы спринг не забыл о том, какие исходные типы были вложены
    Возвращает тип, представляющий прямой суперкласс сущности (класс, интерфейс, примитивный тип или void), представленный этим классом.
    Если суперкласс является параметризованным типом, то возвращается объект Type должен точно отражать фактические параметры типа,
    используемые в источнике код */
    public static final ParameterizedTypeReference<ResponseDto<StockResponseDto>> STOCK_RESPONSE =
            new ParameterizedTypeReference<>() {
            };


    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(StockTests.class, jsonFileName, dtoClass);
    }

    //делаем переменную статиком и пишем туда результат выполнения метода setupUrl
    public static String resourceUrl;

    //переменную с jsonReq и Resp выносим статиком чтобы в каждом методе не создавать лишний раз
    private static final String jsonFileNameReq = "/stock/addStock.req.json";

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
            "44|Склада с id = 44 не существует!",
            "четыре|ID склада должен быть указан числом! Ошибка ввода в: id, со значением value: четыре"})
        //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {

        //формирует ответ сервера (выполнение метода /get при неправильном id)
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
        testRestTemplate.exchange(resourceUrl + id, HttpMethod.DELETE, null, Void.class);

        //проверка на выполнение метода delete()
        testInvalidGet(String.valueOf(id), "Склада с id = " + id + " не существует!");
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
        RequestEntity<StockRequestDto> requestEntityUpd =  // body(stockRequestDto) - берём сущность по полученному id.
                RequestEntity.put(URI.create(resourceUrl + id)).contentType(MediaType.APPLICATION_JSON)
                        .body(stockRequestDto);

        //выполнение метода /put и ответ от сервера
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntityUpd, Void.class);

        //проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //выполнение теста get() чтобы проверить, берётся Entity с новыми данными
        testGet(String.valueOf(id), getStockResponseDto(jsonFileNameAfterUpd));
    }

    //выполняет get-запрос и проверку ожидаемого и запрашиваемого
    void testGet(String id, StockResponseDto stockResponseDto) {

        /* подставляем id(взятый из новосозданной сущности) в url и сверяем с тем, что получилось
         * Вместо Wrapper. Формируем ответ сервера (выполнение метода /get) */
        ResponseEntity<ResponseDto<StockResponseDto>> responseEntity =
                testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);

        StockResponseDto data = responseEntity.getBody().getData();

        //всякме тесты на соответствие и тд
        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(stockResponseDto.getName());
        assertThat(data.getCity()).isEqualTo(stockResponseDto.getCity());
    }

    //Создаёт entity и получаем id entity из бд
    private Long createStock(StockRequestDto stockRequestDto) {

        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<StockRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).body(stockRequestDto);//в body пихаем dto

        //получаем только id из бд, чтобы не тащить все данные оттуда (в контроллере надо вернуть значение id после записи в бд)
        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);

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
}
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




