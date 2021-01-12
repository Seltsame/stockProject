package ru.rosketscience.test.stockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {

    List<StockPlace> findAllByStockId(Long id);

    Optional<StockPlace> getById(Long id);

    //метод для получения максимального номера места в ряду, если ряд будет не найден,
    // то он вернет значение 1, если найден, то максимальное значение ряда
    @Query("SELECT coalesce(max(sp.shelf), 1)  FROM StockPlace sp WHERE sp.row=:row")
    int getMaxShelfNumber(@Param("row") String row);

    //вывод суммы вместимости полок по id склада
    @Query("SELECT SUM(sp.capacity) FROM StockPlace sp " +
            "JOIN Stock st on st = sp.stock WHERE sp.stock.id = :stock_id ")
    Long getSumStockPlaceCapacity(@Param("stock_id") Long stock_id);

    @Query("FROM StockPlace sp " +
            "JOIN Stock st ON st = sp.stock WHERE st.id = :stock_id " +
            "ORDER BY sp.shelf")
    List<StockPlace> findStockPlacesByStockId(@Param("stock_id") Long stock_id);
}
