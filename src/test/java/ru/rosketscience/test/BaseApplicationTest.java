package ru.rosketscience.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//RANDOM_PORT, чтобы использовался случайный порт, а не 8080.
@ActiveProfiles("test") //выбор профиля работы приложения, прописанного в properties
@Testcontainers
//Базовый класс с настройками тестов
public class BaseApplicationTest {

    //объект для фиксации типа generic, чтобы метод getBody() возвращал нужный типизированный результат
    //(возможно использование Wrapper)
    @Container
    //бин с настройками бд (Username, Password и dbName теперь берутся из Container == @DynamicPropertySource)
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.10");

    @DynamicPropertySource //подключение к бд в Docker
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    TestRestTemplate testRestTemplate; //Http-клиент

    @Autowired
    ObjectMapper objectMapper; // Spring'овый(!)

    //добавление рандомного порта на тест
    @LocalServerPort
    protected int port;


    public static String resourceUrl;

    //создаем метод, который возвращает предыдущий метод, только с вложенным objectMapper
    protected <T> T getObjectFromResourceJson(Class<?> testClass, String jsonFileName, Class<T> objectClass) {
        return Utils.getObjectFromResourceJsonObjMap(objectMapper, testClass, jsonFileName, objectClass);
    }
}
