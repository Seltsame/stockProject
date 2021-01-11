package ru.rosketscience.test.stock;

import lombok.*;
import ru.rosketscience.test.stockPlace.StockPlace;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
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
