package ru.rocketscience.test.stockPlace;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {
}
