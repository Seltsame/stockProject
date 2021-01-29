package ru.rocketscience.test.common;

import lombok.Value;

@Value
public class ResponseDto<T> { //DTO для обработки ошибок
    // <T> - входящий дата с любыми параметрами
    String error;
    T data;

     /*List<String> errors - лист ошибок пользователю;
    List<String> error_codes; код ошибок для UI
    T data;
    E errorData; - например, сколько попыток входа осталось
    */
}
