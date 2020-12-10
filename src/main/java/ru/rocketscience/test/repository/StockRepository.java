package ru.rocketscience.test.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.rocketscience.test.model.Stock;


@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {
}
