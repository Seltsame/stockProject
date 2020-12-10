package ru.rocketscience.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.rocketscience.test.dto.StockResponseDto;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class StockTests {

    @Autowired
    TestRestTemplate restTemplate; //Http-клиент

    //тестирование get-метода
    @Test
    void testGet() {
        String resourceUrl = "http://localhost:8080/stock/get/2";
        StockResponseDto response = restTemplate.getForObject(resourceUrl, StockResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Морской порт");
        assertThat(response.getCity()).isEqualTo("Морской город");
    }
}
