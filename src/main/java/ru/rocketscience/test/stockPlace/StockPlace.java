package ru.rocketscience.test.stockPlace;

import lombok.*;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rocketscience.test.stock.Stock;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StockPlace {

    /*
     * При OneToMany, ManyToOne, ManyToMany, OneToOne , при использовании Lombok:
     * Когда Entity ссылается на Entity (OneToMany, ManyToOne, ManyToMany, OneToOne), то надо исключить из нее переопределение toString и equalsAndHashCode
     * желательно всех полей, кроме ID
     * вложенные сущности, для того, чтобы избежать рекурсивного вызова методов: т.к. при переопределении они по цепочке лезут друг
     * за другом и в конечном итоге доходят до List/Set.. и котором Hibernate-proxy, который начинает разворачивать List/Set,
     * которые ссылаются на начальные методы и это приводит к рекурсии.
     * Обычно достаточно ID для переопределения
     *
     * Если связь однонаправленная, то не надо
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ToString.Exclude //помеченное поле для переопределение методов
    @EqualsAndHashCode.Include  //помеченное поле для переопределение методов
    private Long id;

    private String row;
    private int shelf;
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Override
    public String toString() {
        return "StockPlace{" +
                "id=" + id +
                ", row='" + row + '\'' +
                ", shelf=" + shelf +
                ", capacity=" + capacity +
                ", stock=" + stock +
                ", stockPlaceOnStockPlaceSet=" + stockPlaceOnStockPlaceSet +
                '}';
    }
}
