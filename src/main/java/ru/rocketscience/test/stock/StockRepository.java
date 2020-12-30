package ru.rocketscience.test.stock;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    Optional<Stock> getById(Long id);

    List<Stock> findAllByCity(String cityName);

   // List<Stock> getAllStocks();

}
