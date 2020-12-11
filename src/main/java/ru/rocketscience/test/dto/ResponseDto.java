package ru.rocketscience.test.dto;

import lombok.Value;

@Value
public class ResponseDto<T> { //DTO для обработки ошибок
    // <T> - входящий дата с любыми параметрами

    String error;
    T data;
}
