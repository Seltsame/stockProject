package ru.rocketscience.test.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> getById(Long id);

    //сумма количества товаров на полке по id полки
    @Query("SELECT sum(psp.quantityProduct) FROM ProductOnStockPlace psp " +
            "JOIN StockPlace sp on sp = psp.stockPlace WHERE sp.id = :stockPlaceId ")
    long getSumQuantityProductByStockPlaceId(@Param("stockPlaceId") Long stockPlaceId);

}
