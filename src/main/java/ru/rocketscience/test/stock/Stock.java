package ru.rocketscience.test.stock;

import lombok.*;
import ru.rocketscience.test.stockPlace.StockPlace;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Stock {
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
    @EqualsAndHashCode.Include  //помеченное поле для переопределение методов
    Long id;

    private String name;
    private String city;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)

    List<StockPlace> stockPlace;
}
