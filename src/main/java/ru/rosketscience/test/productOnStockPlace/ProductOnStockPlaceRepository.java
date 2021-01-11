package ru.rosketscience.test.productOnStockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductOnStockPlaceRepository extends CrudRepository<ProductOnStockPlace, Long> {

    //товар на полке по id склада
    @Query("SELECT psp FROM ProductOnStockPlace psp JOIN Stock s ON s.id = psp.stockPlace.stock.id where s.id =:stock_id")
    Set<ProductOnStockPlace> getProductOnStockPlaceByStockId(@Param("stock_id") Long stock_id);



    //правильность написания метода
   /* @Query("SELECT coalesce(max(quantityProduct), 0) FROM ProductOnStockPlace WHERE stockPlace.id =:stock_place_id")
    int getMaxQuantityProduct(@Param("stock_place_id") Long stock_place_id);*/

    //выбор максимального  количества продукта в stockPlace конкретного stock по stock_ID
    @Query("SELECT q.quantityProduct FROM ProductOnStockPlace q JOIN StockPlace psp on psp.stock.id = :stock_id")
    int getMaxQuantityProduct(@Param("stock_id") Long stockId);

}
