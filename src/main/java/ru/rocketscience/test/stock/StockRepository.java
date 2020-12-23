package ru.rocketscience.test.stock;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface StockRepository extends CrudRepository<Stock, Long> {
}
