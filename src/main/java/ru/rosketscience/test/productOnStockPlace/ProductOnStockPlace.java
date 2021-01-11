package ru.rosketscience.test.productOnStockPlace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rosketscience.test.product.Product;
import ru.rosketscience.test.stockPlace.StockPlace;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOnStockPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    int quantityProduct;

    @ManyToOne
    @JoinColumn(name = "stock_place_id", foreignKey = @ForeignKey(name = "stock_place_on_stock_place_to_stock_place"))
    StockPlace stockPlace;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "product_on_stock_place_to_product"))
    Product product;
}
