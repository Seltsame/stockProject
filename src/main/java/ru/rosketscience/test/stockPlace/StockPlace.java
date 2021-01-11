package ru.rosketscience.test.stockPlace;

import lombok.*;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rosketscience.test.stock.Stock;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class StockPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String row;
    private int shelf;
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "stock_place_to_stock"))
    Stock stock;

    /*@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_stock_place",
            joinColumns = @JoinColumn(name = "stock_place_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    List<Product> productList;*/

/*    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "stock_place_to_product"))
    List<Product> productList;*/
/*
    @OneToMany(mappedBy = "stockPlace")
    List<Product> productList;*/

    @OneToMany(mappedBy = "stockPlace")
    Set<ProductOnStockPlace> stockPlaceOnStockPlaceSet;
}
