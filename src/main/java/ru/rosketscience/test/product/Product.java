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
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //переопределение методов ТОЛЬКО для помеченных полей.
public class Product {

    /*
     * При OneToMany ManyToOne, при использовании Lombok
     *
     * Когда Entity ссылается на Entity (OneToMany, ManyToOne, ManyToMany, OneToOne), то надо исключить из нее переопределение toString и equalsAndHashCode
     * вложенные сущности, для того, чтобы избежать рекурсивного вызова методов: т.к. при переопределении они по цепочке лезут друг
     * за другом и в конечном итоге доходят до List/Set.. и котором Hibernate-proxy, который начинает разворачивать List/Set,
     * которые ссылаются на начальные методы и это приводит к рекурсии.
     * Обычно достаточно ID для переопределения
     *
     * Если связь однонаправленная, то не надо
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude //помеченное поле для переопределение методов
    @EqualsAndHashCode.Include  //помеченное поле для переопределение методов
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

