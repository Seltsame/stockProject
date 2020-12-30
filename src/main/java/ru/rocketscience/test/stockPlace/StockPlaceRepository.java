package ru.rocketscience.test.stockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {

    //метод для получения максимального номера полки
    @Query("SELECT coalesce(max(shelf), 0) from StockPlace")
    int getMaxShelfNumber();

    @Query("SELECT quantity_product FROM  ")
    int getMaxShelfCapacity();

    List<StockPlace> findAllById(Long id);

    List<StockPlace> findAllByStockId(Long id);
}
