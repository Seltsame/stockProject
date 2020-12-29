package ru.rocketscience.test.stockPlace;

import lombok.Data;
import ru.rocketscience.test.stock.Stock;

import javax.persistence.*;

@Entity
@Data
public class StockPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String row;
    private int rack;
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "stock_place_to_stock"))
    Stock stock;
}
