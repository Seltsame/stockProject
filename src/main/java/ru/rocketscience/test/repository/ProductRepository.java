package ru.rocketscience.test.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.rocketscience.test.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
}
