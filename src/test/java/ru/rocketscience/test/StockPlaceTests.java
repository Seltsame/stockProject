package ru.rocketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rocketscience.test.stockPlace.ManyStockPlacesResponseDto;
import ru.rocketscience.test.stockPlace.StockPlaceRequestDto;
import ru.rocketscience.test.common.ResponseDto;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class StockPlaceTests extends BaseApplicationTest {

    /* public static class StockDtoWrapper {
        public StockResponseDto data;
    }
    //использование Wrapper
    StockDtoWrapper wrapper = testRestTemplate.getForObject(resourceUrlId, StockDtoWrapper.class);
    StockResponseDto data = wrapper.data; */


    public final ParameterizedTypeReference<ResponseDto<StockPlaceResponseDto>> STOCKPLACE_RESPONSE
            = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<ResponseDto<ManyStockPlacesResponseDto>> ADD_STOCKPLACE_RESPONSE
            = new ParameterizedTypeReference<>() {
    };//свобдное место после добавления полочки

    public static final ParameterizedTypeReference<ResponseDto<Long>> LONG_RESPONSE
            = new ParameterizedTypeReference<>() {
    };

    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(StockPlaceTests.class, jsonFileName, dtoClass);
    }

    private static final String jsonFileNameReq = "/stockPlace/addStockPlace.req.json";

   /* @BeforeEach
    public void setupUrl() {
        stockPlaceUrl = "http://localhost:" + port + "/stockplace/";
    }*/

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "407|Места с id = 407 не существует!",
            "сорок|Id места должен быть указан числом! Ошибка ввода в: id, со значением value: сорок"})
    void testInvalidGet(String id, String expectedMessage) {
        ResponseEntity<ResponseDto<StockPlaceResponseDto>> responseEntity
                = testRestTemplate.exchange(stockUrl + id, HttpMethod.GET, null,
                STOCKPLACE_RESPONSE);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getError()).isEqualTo(expectedMessage);
    }

    @Test
    void simpleGet() {
        testGet("1", getStockPlaceResponseDto("/stockPlace/addStockPlace.resp.json"));
    }

    @Test
    void testAdd() {
        createAndTestStockPlace(jsonFileNameReq);
    }

    @Test
    void testDelete() {
        Long id = createStockPlace(getStockPlaceRequestDto(jsonFileNameReq));
        testRestTemplate.exchange(stockUrl + id, HttpMethod.DELETE, null, Void.class);
        testInvalidGet(String.valueOf(id), "Места с id = " + id + " не существует!");
    }

    @Test
    void testUpdate() {
        String jsonFileNameBeforeUpd = "/stockPlace/updateStockPlace.req.json";
        String jsonFileNameAfterUpd = "/stockPlace/updateStockPlace.resp.json";

        StockPlaceResponseDto stockPlaceResponseDtoUpd
                = getStockPlaceResponseDto(jsonFileNameAfterUpd);

        Long id = createAndTestStockPlace(jsonFileNameBeforeUpd);

        RequestEntity<StockPlaceRequestDto> requestEntityUpd
                = RequestEntity.put(URI.create(stockUrl + id)).contentType(MediaType.APPLICATION_JSON)
                .body(getStockPlaceRequestDto(jsonFileNameAfterUpd));

        ResponseEntity<Void> responseEntityUpd = testRestTemplate.exchange(requestEntityUpd, Void.class);
        assertThat(responseEntityUpd.getStatusCode()).isEqualTo(HttpStatus.OK);
        testGet(String.valueOf(id), stockPlaceResponseDtoUpd);
    }

    @Test
    void testAddManyStockPlaces() {
        String resourceUrlAddStockPlaces = stockUrl + "addStockPlaces/";
        StockPlaceRequestDto stockPlaceRequestDto
                = getStockPlaceRequestDto("/stockPlace/addManyStockPlaces.req.json");

        RequestEntity<StockPlaceRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlAddStockPlaces))
                .contentType(MediaType.APPLICATION_JSON)
                .body(stockPlaceRequestDto);

        testRestTemplate.exchange(requestEntity, Void.class);
        ResponseEntity<ResponseDto<Long>> responseEntity
                = testRestTemplate.exchange(stockUrl + "maxCapacityInStock/"
                        + stockPlaceRequestDto.getStockId(),
                HttpMethod.GET, null, LONG_RESPONSE);

        long expectedStockCapacity = 20;
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getData())
                .isEqualTo(expectedStockCapacity);


    }

    void testGet(String id, StockPlaceResponseDto stockPlaceResponseDto) {
        ResponseEntity<ResponseDto<StockPlaceResponseDto>> responseEntity
                = testRestTemplate.exchange(stockUrl + id, HttpMethod.GET, null, STOCKPLACE_RESPONSE);
        assertThat(responseEntity.getBody()).isNotNull();
        StockPlaceResponseDto data = responseEntity.getBody().getData();

        //использование этого метода позволяет сравнивать только по выбранным полям классов
        assertThat((data)).isEqualToComparingOnlyGivenFields(stockPlaceResponseDto,
                "row", "shelf", "capacity");
    }

    //создаём сущность сразу requestDto созданную из JSONки
    private Long createStockPlace(StockPlaceRequestDto stockPlaceRequestDto) {
        RequestEntity<StockPlaceRequestDto> requestEntity
                = RequestEntity.post(URI.create(stockUrl)).contentType(MediaType.APPLICATION_JSON)
                .body(stockPlaceRequestDto);

        Long id = testRestTemplate.postForObject(stockUrl, requestEntity, Long.class);
        assertThat(id).isNotNull();
        return id;
    }

    //метод для создание, тестирования Entity и возврат его ID
    private Long createAndTestStockPlace(String jsonFileName) {
        //создаём entity напрямую из json объекта
        Long id = createStockPlace(getStockPlaceRequestDto(jsonFileName));
        //проверяем тестом responseDto. Делается для того, чтобы было понятно, что по stock_id лежит
        testGet(String.valueOf(id), getStockPlaceResponseDto(jsonFileName));
        return id;
    }

    private StockPlaceRequestDto getStockPlaceRequestDto(String jsonFileNameReq) {
        return getFromJson(jsonFileNameReq, StockPlaceRequestDto.class);
    }

    private StockPlaceResponseDto getStockPlaceResponseDto(String jsonFileNameResp) {
        return getFromJson(jsonFileNameResp, StockPlaceResponseDto.class);
    }
}
