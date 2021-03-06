package ru.rocketscience.test.productOnStockPlace;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductOnStockPlaceRepository extends CrudRepository<ProductOnStockPlace, Long>, JpaSpecificationExecutor<ProductOnStockPlace> {

    Optional<ProductOnStockPlace> getByStockPlaceId(Long stockPlaceId);

    void deleteByProductId(Long id);

   /* Первая реализация запроса и метода для получения stockPlace - freeSpace. В query генерируется запрос и пишется
    сразу в Map.
    Стрим в Integer[] из id полки и сумме товаров на полке по id склада, запрос на вывод сразу в Map
    id - количество товара на месте.*/
   /*  @Query("SELECT new javafx.util.Pair(sp.id, sum(psp.quantityProduct)) FROM ProductOnStockPlace psp " +
            "JOIN StockPlace sp ON sp = psp.stockPlace " +
            "where sp.id =:stock_id GROUP BY sp")
    Stream<javafx.util.Pair<Long, Long>>getQuantityProductOnStockPlaceByStockId(@Param("stock_id") Long stock_id);*/

    //вторая из реализаций запроса и метода для получения stockPlace - freeSpace
    @Query(" FROM ProductOnStockPlace psp " +
            "JOIN StockPlace sp ON sp = psp.stockPlace JOIN Stock st ON st = sp.stock WHERE st.id =:stockId")
    Set<ProductOnStockPlace> getProductOnStockPlaceByStockId(@Param("stockId") Long stockId);

    //выбор максимального количества продукта в stockPlace конкретного stock по stock_ID
    @Query("SELECT sum(psp.quantityProduct) FROM ProductOnStockPlace psp " +
            "JOIN StockPlace sp on sp = psp.stockPlace WHERE sp.stock.id = :stockId ")
    long getSumQuantityProductByStockId(@Param("stockId") Long stockId);

    //Лучше взять объект и потом с ним работать
    @Query("FROM ProductOnStockPlace psp WHERE psp.stockPlace.id = :stockPlaceId AND psp.product.id = :productId")
    ProductOnStockPlace getProductOnStockPlaceByStockPlaceAndProduct(@Param("stockPlaceId") Long stockPlaceId, @Param("productId") Long productId);

    List<ProductOnStockPlace> findAll(Specification<ProductOnStockPlace> specification);
}
