package ru.rocketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;

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

    public static String resourceUrl; //делаем переменную статиком и пишем туда результат выполнения метода setupUrl

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
        testGet("2", "Морской склад", "Морской город");
    }

    /* 1. тестирование get-метода отрицательный сценарий, если склада не существует (введен не тот id)
     * 2. тестирование get-метода отрицательный сценарий, если id склада указан String*/

    @ParameterizedTest // краткая запись для нескольких тестов на один и тот же функционал с разными параметрами
    @CsvSource(delimiter = '|', value = { //value - наборы параметров, delimiter - разделитель
            "44|Склада с id = 44 не существует",
            "четыре|ID склада должен быть указан числом! Ошибка ввода в: id, со значением value: четыре"})
    //тест-метод /get с неправильным id
    void testInvalidGet(String id, String expectedMessage) {

        // Формируем ответ сервера (выполнение метода /get при неправильном id)
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    /*  тестирование add-метода положительный сценарий
     При добавлении проще и правильнее проверять по id добавленного объекта */
    @Test
    void testAdd() {

        String stockName = "Имя склада";
        String cityName = "Имя города";
        //вытаскиваем id из метода создания Stock
        Long id = createStock(stockName, cityName);

        testGet(String.valueOf(id), stockName, cityName);
    }

    //тест delete-метода
    @Test
    void testDelete() {

        String nameToDel = "Новый склад";
        String cityNameToDel = "Новый город";

        Long stockId = createStock(nameToDel, cityNameToDel);

        //выполнение метода /del
        testRestTemplate.exchange(resourceUrl + stockId, HttpMethod.DELETE, null, STOCK_RESPONSE);

        //проверка на выполнение метода delete()
        testInvalidGet(String.valueOf(stockId), "Склада с id = " + stockId + " не существует");
    }

    //тест update-метода
    @Test
    void testUpdate() {

        //старые данные
        String stockName = "Имя склада";
        String cityName = "Имя города";

        Long id = createStock(stockName, cityName);

        //проверка на то существование
        testGet(String.valueOf(id), stockName, cityName);

        //новые данные для перезаписи
        String stockUpdName = "Update stock";
        String cityUpdName = "Update city";

        //создаем дто с новой сущностью
        StockRequestDto stockRequestDto = createStockRequestDto(stockUpdName, cityUpdName);

        //формирует Http-запрос на сервер для получения данных об Entity
        RequestEntity<StockRequestDto> requestEntityUpd =  // body(stockRequestDto) - берём сущность по полученному id.
                RequestEntity.put(URI.create(resourceUrl + id)).contentType(MediaType.APPLICATION_JSON).body(stockRequestDto);

        //выполнение метода /put и ответ от сервера
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(requestEntityUpd, Void.class);

        //проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //выполнение теста get() чтобы проверить, берётся Entity с новыми данными
        testGet(String.valueOf(id), stockUpdName, cityUpdName);
    }

    //выполняет get-запрос и проверку ожидаемого и запрашиваемого
    void testGet(String id, String stockName, String cityName) {

        /* подставляем id(взятый из новосозданной сущности) в url и сверяем с тем, что получилось
         * Вместо Wrapper. Формируем ответ сервера (выполнение метода /get) */
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, STOCK_RESPONSE);
        StockResponseDto data = response.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(stockName);
        assertThat(data.getCity()).isEqualTo(cityName);
    }

    //Создаём entity и получаем id entity из бд
    private Long createStock(String stockName, String cityName) {

        StockRequestDto request = createStockRequestDto(stockName, cityName);
        //формирует Http-запрос с DTO новой сущности для получения данных об Entity
        RequestEntity<StockRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).body(request);
        /* получаем только id из бд, чтобы не тащить все данные оттуда (в контроллере надо вернуть значение id после записи
         в бд)*/
        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);

        assertThat(id).isNotNull();
        return id;
    }

    //Тестовый объект для записи
    private StockRequestDto createStockRequestDto(String stockName, String cityName) {

        return StockRequestDto.builder()
                .name(stockName)
                .city(cityName)
                .build();
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




