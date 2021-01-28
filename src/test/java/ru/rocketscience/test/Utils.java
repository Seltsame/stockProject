package ru.rocketscience.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.InputStream;

//@ - аннотация спец Util-class
@UtilityClass
//специальный класс для ковертации JSON в POJO и наоборот
@Slf4j
public class Utils {
    /*
        метод для ковертации Json в Pojo:
        <T> - принимаем  параметр такого типа <T>
         T - возвращаем объект с этим типом.
        <?> - любые параметры */

    public static <T> T getObjectFromResourceJsonObjMap(ObjectMapper objectMapper, Class<?> testClass, String jsonFileName, Class<T> objectClass) {

        //ObjectMapper класс-конвертер -> см.BaseApplicationTest.class
        //getResourceAsStream() - метод, который берёт JSON-файл и конвертирует в InputStream
        try (InputStream resourceAsStream = testClass.getResourceAsStream(jsonFileName)) {
            //readValue() - основной метод для преобразования JSON в POJO, objectClass - указанного класса, кодировки UTF-8
            return objectMapper.readValue(IOUtils.toString(resourceAsStream, "UTF-8"), objectClass);
        } catch (Exception e) { //любая ошибка == LOG!!!!!
            String errMessage = e.getMessage();
            log.error("getObjectFromResourceJsonObjMap finished with exception: {}", errMessage);
            throw new RuntimeException(e);
        }
    }
}
