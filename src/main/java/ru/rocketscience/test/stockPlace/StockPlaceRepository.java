package ru.rocketscience.test.stockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {

    //метод для получения максимального номера полки
    @Query(value = "SELECT coalesce(max(shelf), 0) from StockPlace")
    int getMaxShelfNumber();
}
