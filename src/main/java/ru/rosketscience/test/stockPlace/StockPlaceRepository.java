package ru.rosketscience.test.stockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {

    //метод для получения максимального номера места в ряду
    @Query("SELECT coalesce(max(sp.shelf), 0)  FROM StockPlace sp WHERE sp.row=:row")
    int getMaxShelfNumber(@Param("row") String row);

    //   @Query("SELECT q.quantityProduct FROM ProductOnStockPlace q JOIN StockPlace psp on psp.stock.id = :stock_id")
    @Query("SELECT sp.capacity FROM StockPlace sp WHERE sp.stock.id = :stock_id")
    int getStockPlaceCapacity(@Param("stock_id") Long stock_id);

    List<StockPlace> findAllByIdOrderByShelf(Long id);

    List<StockPlace> findAllByStockId(Long id);

    Optional<StockPlace> getById(Long id);
}

/*
SELECT StockPlace.row, StockPlace.shelf, StockPlace.capacity, Product.quantityProduct " +
            "FROM StockPlace JOIN Product ON StockPlace .id = Product .stockPlace.id */

// SELECT * - ? надо тестить, нужен ли on?
    /*@Query("SELECT q.productOnStockPlaceSet FROM Product q JOIN q.productOnStockPlaceSet psp WHERE psp.stockPlace.id =: id")
    //все productOnStockPlace где StockPlace_id
    Set<ProductOnStockPlace> getAllProductsByStockPlaceId(@Param("id") Long id);*/

  /*  @Query("SELECT q FROM Product q JOIN ProductOnStockPlace prSt " +
            "on prSt.product.id = q.id where q.id=:id")
    int findProductQuantity(@Param("id") Long id);*/