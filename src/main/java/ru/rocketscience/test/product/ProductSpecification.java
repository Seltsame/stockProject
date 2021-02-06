package ru.rocketscience.test.product;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rocketscience.test.stock.Stock;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;

@Component
public class ProductSpecification {

    public Specification<Product> findByNameAndPrice(final String name, final BigDecimal minPrice, final BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            //StringUtils.isEmpty():
            //StringUtils.trimAllWhitespace():
            Predicate predicate = criteriaBuilder.conjunction();
            // Predicate predicate = null;
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(name))) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (minPrice != null) {
                predicate = criteriaBuilder.and(
                        predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = criteriaBuilder.and(
                        predicate, criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return predicate;
        };
    }

    public Specification<Product> findByCityProduct(String city, String product) {
        return Specification.where((root, query, criteriaBuilder) -> {
            Predicate result = null;
            Join<Product, ProductOnStockPlace> productJoin = root.join("productOnStockPlaceSet");
            Join<ProductOnStockPlace, Stock> stock = productJoin
                    .join("stockPlace")
                    .join("stock");
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(city))) {
                result = criteriaBuilder.like(stock.get("city"), "%" + city + "%");
            }
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(product))) {
                result = criteriaBuilder.like(root.get("name"), "%" + product + "%");
            }
            return result;
        });
    }
}
  /*  public final EntityManager entityManager;

    public ProductSpecification(EntityManager entityManager) {
        this.entityManager = entityManager;
    }*/
    /*
        public Specification<Product> findByNameAndPrice(final String name, final BigDecimal minPrice, BigDecimal maxPrice, String price) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
            Root<Product> product = query.from(Product.class);
            query.select(product)
                    .where(criteriaBuilder.or(criteriaBuilder.like(product.get("name"), "%" + name + "%"),
                            criteriaBuilder.between(product.get("price"), minPrice, maxPrice)));
        }*/
