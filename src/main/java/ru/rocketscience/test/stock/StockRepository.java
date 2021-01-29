package ru.rocketscience.test.stock;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {

    Optional<Stock> getById(Long id);

    List<Stock> findAllByCityOrderByName(String cityName);

    List<Stock> findAll(Specification<Stock> specification);
}
