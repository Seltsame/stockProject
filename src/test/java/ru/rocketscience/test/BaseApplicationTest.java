package ru.rocketscience.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//RANDOM_PORT, чтобы использовался случайный порт, а не 8080.
@ActiveProfiles("test") //выбор профиля работы приложения, прописанного в properties
@ContextConfiguration(initializers = PostgresInitializer.class)
@Testcontainers
//Базовый класс с настройками тестов
public class BaseApplicationTest {

    //объект для фиксации типа generic, чтобы метод getBody() возвращал нужный типизированный результат
    //(возможно использование Wrapper)
//    @Container
//    //бин с настройками бд (Username, Password и dbName теперь берутся из Container == @DynamicPropertySource)
//    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.10");

    @DynamicPropertySource //подключение к бд в Docker
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.url", PostgresInitializer::getJdbcUrl);
    }

    @Autowired
    TestRestTemplate testRestTemplate; //Http-клиент

    @Autowired
    ObjectMapper objectMapper; // Spring'овый(!)

    //добавление рандомного порта на тест
    @LocalServerPort
    protected int port;

    public static String resourceUrl;
    public static String productUrl;
    public static String stockUrl;
    public static String stockPlaceUrl;

    @BeforeEach
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port;
    }

    //создаем метод, который возвращает предыдущий метод, только с вложенным objectMapper
    protected <T> T getObjectFromResourceJson(Class<?> testClass, String jsonFileName, TypeReference<T> typeReference) {
        return Utils.getObjectFromResourceJsonObjMap(objectMapper, testClass, jsonFileName, typeReference);
    }

    //создаем метод, который возвращает предыдущий метод, только с вложенным objectMapper
    protected <T> List<T> getObjectListFromResourceJson(Class<?> testClass, Class<? extends Collection> type, String jsonFileName, Class<T> objectClass) {
        return Utils.getObjectFromResourceJsonObjMapToCollection(objectMapper, testClass, type, jsonFileName, objectClass);
    }
}
