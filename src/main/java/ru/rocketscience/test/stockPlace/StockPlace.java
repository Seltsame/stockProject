package ru.rocketscience.test.stockPlace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rocketscience.test.product.Product;
import ru.rocketscience.test.stock.Stock;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String row;
    private int shelf;
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "stock_place_to_stock"))
    Stock stock;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_stock_place",
            joinColumns = @JoinColumn(name = "stock_place_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    List<Product> productList;

/*    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "stock_place_to_product"))
    List<Product> productList;*/

}
