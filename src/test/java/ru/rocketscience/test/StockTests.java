package ru.rocketscience.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.rocketscience.test.dto.ResponseDto;
import ru.rocketscience.test.dto.StockResponseDto;

import static org.assertj.core.api.Assertions.assertThat;


//RANDOM_PORT, чтобы использовался случайный порт, а не 8080.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //выбор профиля работы приложения, прописанного в properties
@Testcontainers
class StockTests {

    public static final ParameterizedTypeReference<ResponseDto<StockResponseDto>> STOCK_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    @Container //бин с настройками бд
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.10")
            .withDatabaseName("horse")
            .withUsername("postgres")
            .withPassword("qwerty");

    @DynamicPropertySource //подключение к бд в Docker
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }


    @Autowired
    TestRestTemplate restTemplate; //Http-клиент

    //добавление рандомного порта на тест
    @LocalServerPort
    protected int port;


    //тестирование get-метода положительный сценарий
    @Test
    void testGet() {
        String resourceUrl = "http://localhost:" + port + "/stock/get/2";
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StockResponseDto data = response.getBody().getData();
        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo("Морской порт");
        assertThat(data.getCity()).isEqualTo("Морской город");
    }

    //тестирование get-метода отрицательный сценарий, если пользователя не существует
    @Test
    void testInvalidGet() {
        String resourceUrl = "http://localhost:" + port + "/stock/get/44";
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Склада с id = 44 не существует");

    }

    //тестирование get-метода отрицательный сценарий, если пользователя не существует
    @Test
    void testInvalidStringGet() {
        String resourceUrl = "http://localhost:" + port + "/stock/get/четыре";
        ResponseEntity<ResponseDto<StockResponseDto>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, STOCK_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody().getError()).isEqualTo("Номер склада должен быть указан числом! Ошибка ввода в: id, со значением value: четыре");

    }
}
