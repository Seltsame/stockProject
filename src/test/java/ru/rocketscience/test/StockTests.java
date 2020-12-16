package ru.rocketscience.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.StockResponseDto;
import ru.rocketscience.test.dto.request.StockRequestDto;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

//RANDOM_PORT, чтобы использовался случайный порт, а не 8080.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //выбор профиля работы приложения, прописанного в properties
@Testcontainers
class StockTests {

    //объект для фиксации типа generic, чтобы метод getBody() возвращал нужный типизированный результат
    public static final ParameterizedTypeReference<ResponseDto<StockResponseDto>> STOCK_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    @Container //бин с настройками бд (Username, Password и dbName теперь берутся из Container == @DynamicPropertySource)
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.10");

    @DynamicPropertySource //подключение к бд в Docker
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    TestRestTemplate testRestTemplate; //Http-клиент

    //добавление рандомного порта на тест
    @LocalServerPort
    protected int port;

    /* Для чего выносить код в отдельные методы:
    1. Избежание дублируемого кода:
    1.1 Когда код повторяется;
    1.2 Когда несколько строчек кода выполняют определённую задачу(см. tesGet()). Нпрмр: если метод не влазит на 1/2 экрана,
    то надо разделять:).
    */

    //тестирование get-метода положительный сценарий
    @Test
    void testGet() {
        testGet("2", "Морской склад", "Морской город");
    }

    /*1. тестирование get-метода отрицательный сценарий, если склада не существует (введен не тот id)
    2. тестирование get-метода отрицательный сценарий, если id склада указан String*/

    @ParameterizedTest // краткая запись для нескольких тестов на один и тот же функционал с разными параметрами
    @CsvSource(delimiter = '|', value = { //value - наборы параметров, delimiter - разделитель
            "44|Склада с id = 44 не существует",
            "четыре|Номер склада должен быть указан числом! Ошибка ввода в: id, со значением value: четыре"})
    void testInvalidGet(String id, String expectedMessage) {
        String resourceUrl = "http://localhost:" + port + "/stock/get/" + id;
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(resourceUrl, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    /*  тестирование add-метода положительный сценарий
     При добавлении проще и правильнее проверять по id добавленного объекта */
    @Test
    void testAdd() {
        String stockName = "Имя склада";
        String cityName = "Имя города";

        String resourceUrl = "http://localhost:" + port + "/stock/add";
        //Тестовый объект для записи
        StockRequestDto request = StockRequestDto.builder()
                .name(stockName)
                .city(cityName)
                .build();
        //формирует Http-запрос
        RequestEntity<StockRequestDto> requestEntity =
                RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).body(request);
        /* получаем только id из бд, чтобы не тащить все данные оттуда (в контроллере надо вернуть значение id после записи
         в бд)*/
        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);

        assertThat(id).isNotNull();

        testGet(String.valueOf(id), stockName, cityName);
    }

    //выполняет get-запрос и проверку ожидаемого и запрашиваемого
    private void testGet(String id, String stockName, String cityName) {

        //подставляем id(взятый из новосозданной сущности) в url и сверяем с тем, что получилось
        String resourceUrlId = "http://localhost:" + port + "/stock/get/" + id;

        //Вместо Wrapper. Формируем ответ
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                testRestTemplate.exchange(resourceUrlId, HttpMethod.GET, null, STOCK_RESPONSE);
        StockResponseDto data = response.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(stockName);
        assertThat(data.getCity()).isEqualTo(cityName);
    }

    /* Wrapper используем, как альтернативный способ получить вложенный ответ из response;
     *  Урезанная копия ResponseDto
     *  Плохо тем, что (частино) копирует ResponseDto */

   /* public static class StockDtoWrapper {
        public StockResponseDto data;
    }

    //использование Wrapper
    StockDtoWrapper wrapper = testRestTemplate.getForObject(resourceUrlId, StockDtoWrapper.class);
    StockResponseDto data = wrapper.data; */
}
