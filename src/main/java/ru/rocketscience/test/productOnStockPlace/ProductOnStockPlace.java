package ru.rocketscience.test.productOnStockPlace;

import lombok.*;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.product.Product;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //переопределение методов ТОЛЬКО для помеченных полей.
public class ProductOnStockPlace {
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
    @EqualsAndHashCode.Include //помеченное поле для переопределение методов
    Long id;

    @Column
    long quantityProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_place_id", foreignKey = @ForeignKey(name = "stock_place_on_stock_place_to_stock_place"))
    StockPlace stockPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "product_on_stock_place_to_product"))
    Product product;

    //переопределяем сами toString.
    @Override
    public String toString() {
        return "ProductOnStockPlace{" +
                "id= " + id +
                ", quantityProduct= " + quantityProduct +
                ", stockPlace.id= " + stockPlace.getId() +
                ", product.id= " + product.getId() +
                '}';
    }
}
