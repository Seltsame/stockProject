package ru.rocketscience.test.stockPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockPlaceRepository extends CrudRepository<StockPlace, Long> {

    //метод для получения максимального номера полки
    @Query("SELECT coalesce(max(shelf), 0) FROM StockPlace")
    int getMaxShelfNumber();

    @Query("SELECT Product.quantityProduct FROM StockPlace JOIN Product " +
            "ON StockPlace .id = Product .stockPlace.id")
    int quantityProduct();

    List<StockPlace> findAllById(Long id);

    List<StockPlace> findAllByStockId(Long id);

    Optional<StockPlace> getById(Long id);
}

/*
SELECT StockPlace.row, StockPlace.shelf, StockPlace.capacity, Product.quantityProduct " +
            "FROM StockPlace JOIN Product ON StockPlace .id = Product .stockPlace.id */