package ru.rocketscience.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.dto.ResponseDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class ProductTests extends BaseApplicationTest {

    public static final ParameterizedTypeReference<ResponseDto<ProductResponseDto>> PRODUCT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    public static String resourceUrl;

    @BeforeEach
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port + "/product/";
    }

    @Test
    void testSimpleGet() {

        testGet("2", "Товар 2", BigDecimal.valueOf(200));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "47|Товара с id = 47 не существует!",
            "пять|ID товара должен быть указан числом! Ошибка ввода в: id, со значением value: пять"})
    void testInvalidGet(String id, String expectedMessage) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo(expectedMessage);
    }

    void testGet(String id, String name, BigDecimal price) {

        ResponseEntity<ResponseDto<ProductResponseDto>> response
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, PRODUCT_RESPONSE);
        ProductResponseDto data = response.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(name);
        assertThat(data.getPrice()).isEqualTo(price);
    }
}
