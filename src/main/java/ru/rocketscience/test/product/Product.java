package ru.rocketscience.test.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rocketscience.test.stockPlace.StockPlace;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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
    private BigDecimal price; //нужно для того, чтобы были null значения, вместо 0. B
    //BigDecimal круче Double, тк не дают погрешности при вычислениях

    @ManyToMany(mappedBy = "productList")
    //@JoinColumn(name = "stock_place_id", foreignKey = @ForeignKey(name = "product_to_stock_place"))
    List<StockPlace> stockPlaceList;
}
