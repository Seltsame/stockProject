package ru.rocketscience.test.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT coalesce(max(quantityProduct), 0) from Product ")
    int getMaxQuantityProduct();

}
