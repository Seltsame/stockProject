package ru.rocketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.rocketscience.test.common.ResponseDto;
import ru.rocketscience.test.stockPlace.StockPlaceListRequestDto;
import ru.rocketscience.test.stockPlace.StockPlaceListResponseDto;
import ru.rocketscience.test.stockPlace.StockPlaceRequestDto;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class StockPlaceTests extends BaseApplicationTest {


    public final ParameterizedTypeReference<ResponseDto<StockPlaceResponseDto>> STOCKPLACE_RESPONSE
            = new ParameterizedTypeReference<>() {
    };

    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(StockPlaceTests.class, jsonFileName, dtoClass);
    }

    private static final String jsonFileNameReq = "/stockPlace/addStockPlace.req.json";

    @BeforeEach
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port + "/stockplace/";
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "407|Места с id = 407 не существует!",
            "сорок|Id места должен быть указан числом! Ошибка ввода в: id, со значением value: сорок"})
    void testInvalidGet(String id, String expectedMessage) {

        ResponseEntity<ResponseDto<StockPlaceResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null,
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

        testRestTemplate.exchange(resourceUrl + id, HttpMethod.DELETE, null, Void.class);

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
                = RequestEntity.put(URI.create(resourceUrl + id)).contentType(MediaType.APPLICATION_JSON)
                .body(getStockPlaceRequestDto(jsonFileNameAfterUpd));

        ResponseEntity<Void> responseEntityUpd = testRestTemplate.exchange(requestEntityUpd, Void.class);

        assertThat(responseEntityUpd.getStatusCode()).isEqualTo(HttpStatus.OK);

        testGet(String.valueOf(id), stockPlaceResponseDtoUpd);
    }

    void testGet(String id, StockPlaceResponseDto stockPlaceResponseDto) {

        ResponseEntity<ResponseDto<StockPlaceResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, STOCKPLACE_RESPONSE);
        StockPlaceResponseDto data = responseEntity.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getRow()).isEqualTo(stockPlaceResponseDto.getRow());
        assertThat(data.getRack()).isEqualTo(stockPlaceResponseDto.getRack());
        assertThat(data.getCapacity()).isEqualTo(stockPlaceResponseDto.getCapacity());
    }

    //создаём сущность сразу requestDto созданную из JSONки
    private Long createStockPlace(StockPlaceRequestDto stockPlaceRequestDto) {

        RequestEntity<StockPlaceRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON)
                .body(stockPlaceRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);

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

    @Test
    void addStockPlaces() {

        String resourceUrlAddStockPlaces = resourceUrl + "/addStockPlaces/";

        StockPlaceListRequestDto stockPlaceListRequestDto
                = getFromJson("/stockPlace/addManyStockPlaces.req.json", StockPlaceListRequestDto.class);

        RequestEntity<StockPlaceListRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrlAddStockPlaces)).contentType(MediaType.APPLICATION_JSON)
                .body(stockPlaceListRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrlAddStockPlaces, requestEntity, Long.class);

        assertThat(id).isNotNull();

        StockPlaceListResponseDto stockPlaceListResponseDto
                = getFromJson("/stockPlace/addManyStockPlaces.resp.json", StockPlaceListResponseDto.class);

        ParameterizedTypeReference<ResponseDto<StockPlaceListResponseDto>> stockPlaceListResponse
                = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ResponseDto<StockPlaceListResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, stockPlaceListResponse);

        StockPlaceListResponseDto data = responseEntity.getBody().getData();

        assertThat(data).isNotNull();
        assertThat(data.getFirstAddedStockPlaceNum()).isEqualTo(stockPlaceListResponseDto.getFirstAddedStockPlaceNum());
    }
}
/* public static class StockDtoWrapper {
        public StockResponseDto data;
    }
    //использование Wrapper
    StockDtoWrapper wrapper = testRestTemplate.getForObject(resourceUrlId, StockDtoWrapper.class);
    StockResponseDto data = wrapper.data; */