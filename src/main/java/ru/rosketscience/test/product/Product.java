package ru.rosketscience.test.product;

import lombok.*;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private BigDecimal price;

    /*@ManyToMany(mappedBy = "productList")
    //@JoinColumn(name = "stock_place_id", foreignKey = @ForeignKey(name = "product_to_stock_place"))
    List<StockPlace> stockPlaceList;*/

   /* @ManyToOne
    @JoinColumn(name = "stock_place_id", foreignKey = @ForeignKey(name = "stock_place_to_product"))
    StockPlace stockPlace;*/

    @OneToMany(mappedBy = "product")
    Set<ProductOnStockPlace> productOnStockPlaceSet;

}

