package ru.rosketscience.test.product;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
@Configuration
public class ProductSpecification {

  /*  public final EntityManager entityManager;

    public ProductSpecification(EntityManager entityManager) {
        this.entityManager = entityManager;
    }*/

    public Specification<Product> findByName(final String name) {
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get("name"), "%" + name + "%"));
    }

    /*
        public Specification<Product> findByNameAndPrice(final String name, final BigDecimal minPrice, BigDecimal maxPrice, String price) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
            Root<Product> product = query.from(Product.class);
            query.select(product)
                    .where(criteriaBuilder.or(criteriaBuilder.like(product.get("name"), "%" + name + "%"),
                            criteriaBuilder.between(product.get("price"), minPrice, maxPrice)));

        }*/
    public Specification<Product> findByNameAndPrice(final String name, final BigDecimal minPrice, final BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            //StringUtils.isEmpty():
            //StringUtils.trimAllWhitespace():
            Predicate result = null;
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(name))) {
                result = criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
            if (minPrice != null) {//вернуть between
                result = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            if (maxPrice != null) {
                result = criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return result;
        };
    }
}