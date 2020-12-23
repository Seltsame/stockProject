package ru.rocketscience.test.product;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private BigDecimal price; //нужно для того, чтобы были null значения, вместо 0. B
    //BigDecimal круче Double, тк не дают погрешности при вычислениях
}
