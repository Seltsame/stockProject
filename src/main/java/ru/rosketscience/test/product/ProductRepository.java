package ru.rosketscience.test.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ProductRepository extends CrudRepository<Product, Long> {

/*
    @Query("SELECT coalesce(max(), 0) from Product ")
    int getMaxQuantityProduct();
*/



   // List<Product> findAllByStockPlace(List<String> productList);

}
