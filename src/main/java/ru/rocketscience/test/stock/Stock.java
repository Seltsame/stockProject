package ru.rocketscience.test.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rocketscience.test.stockPlace.StockPlace;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private String city;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    List<StockPlace> stockPlace;


}
