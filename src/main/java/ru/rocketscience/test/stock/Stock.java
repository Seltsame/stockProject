package ru.rocketscience.test.stock;

import lombok.Data;
import ru.rocketscience.test.stockPlace.StockPlace;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private String city;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    Set<StockPlace> stockPlace;
}
