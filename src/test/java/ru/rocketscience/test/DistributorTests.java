package ru.rocketscience.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ru.rocketscience.test.common.ResponseDto;
import ru.rocketscience.test.distributor.DistributorRequestDto;
import ru.rocketscience.test.distributor.DistributorResponseDto;
import ru.rocketscience.test.stock.Stock;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceResponseDto;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DistributorTests extends BaseApplicationTest {

    public static final ParameterizedTypeReference<ResponseDto<DistributorResponseDto>> RESPONSE_ENTITY
            = new ParameterizedTypeReference<>() {
    };

    //метод для простоты вызова метода getObjectFromResourceJson();
    private <T> T getFromJson(String jsonFileName, Class<T> dtoClass) {
        return getObjectFromResourceJson(DistributorTests.class, jsonFileName, dtoClass);
    }

    //Проверь ссылку на контроллер!!!!!
    @BeforeEach
    public void setupUrl() {
        resourceUrl = "http://localhost:" + port + "/distributor/";
    }

    // тест надо переделывать!!! + переделка json!
    @Test
    void testGetStockListByName() {

        DistributorRequestDto getFromJsonRequest = getDistributorRequestDto("/distributor/addCityName.req.json");
        Long id = createTestEntity(getFromJsonRequest);

        DistributorResponseDto getFromJsonResponse = getDistributorResponseDto("/distributor/AddCityName.resp.json");

        ResponseEntity<ResponseDto<DistributorResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, RESPONSE_ENTITY);

        List<Stock> stockList = responseEntity.getBody().getData().getStockList();
        assertThat(stockList).isNotNull();
        assertThat(stockList).isEqualTo(getFromJsonResponse.getStockList());
    }

    @Test
    void testGetStockPlacesByStockId() {

        DistributorRequestDto getFromJsonRequest
                = getDistributorRequestDto("/distributor/getStockPlacesById.req.json");
        Long id = createTestEntity(getFromJsonRequest);
        DistributorResponseDto getFromJsonResponse = getDistributorResponseDto("/distributor/getStockPlacesById.resp.json");

        ResponseEntity<ResponseDto<DistributorResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, RESPONSE_ENTITY);

        List<StockPlace> stockPlaceList = responseEntity.getBody().getData().getStockPlaceList();
        assertThat(stockPlaceList).isNotNull();
        assertThat(stockPlaceList).isEqualTo(getFromJsonResponse.getStockPlaceList());
    }



    @Test
    void getCapacityStockPlace() {

        DistributorRequestDto getFromJsonRequest = getDistributorRequestDto();
        Long id = createTestEntity(getFromJsonRequest);

        DistributorResponseDto getFromJsonResponse = getDistributorResponseDto("/distributor/getStockPlacesById.resp.json");
        ResponseEntity<ResponseDto<DistributorResponseDto>> responseEntity
                = testRestTemplate.exchange(resourceUrl + id, HttpMethod.GET, null, RESPONSE_ENTITY);
        int capacity = responseEntity.getBody().getData().getCapacity();
        assertThat(capacity).isNotNull();
        assertThat(capacity).isEqualTo(getFromJsonResponse.getCapacity());
    }

    private DistributorRequestDto getDistributorRequestDto() {
        return getDistributorRequestDto("/distributor/getStockPlacesById.req.json");
    }

    @Test
    void getAllCapacity() {

    }

    private Long createTestEntity(DistributorRequestDto distributorRequestDto) {

        RequestEntity<DistributorRequestDto> requestEntity
                = RequestEntity.post(URI.create(resourceUrl)).contentType(MediaType.APPLICATION_JSON).body(distributorRequestDto);

        Long id = testRestTemplate.postForObject(resourceUrl, requestEntity, Long.class);
        assertThat(id).isNotNull();
        return id;
    }

    private DistributorRequestDto getDistributorRequestDto(String jsonFileNameResp) {
        return getFromJson(jsonFileNameResp, DistributorRequestDto.class);
    }

    private DistributorResponseDto getDistributorResponseDto(String jsonFileNameResp) {
        return getFromJson(jsonFileNameResp, DistributorResponseDto.class);
    }
}
