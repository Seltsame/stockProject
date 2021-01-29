package ru.rosketscience.test.productOnStockPlace;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.rosketscience.test.product.Product;
import ru.rosketscience.test.stock.Stock;
import ru.rosketscience.test.stockPlace.StockPlace;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Configuration
public class ProductOnStockPlaceSpecification {
    /*
    SELECT pr.id, pr.name, psp.stock_place_id, psp.quantity_product, st.id, stКак.city
    FROM product_on_stock_place psp

    JOIN product pr
    ON psp.product_id = pr.id

    JOIN stock_place sp
    ON psp.stock_place_id = sp.id
    JOIN stock st
    ON st.id = sp.stock_id
    WHERE st.city like '%о%';*/

    // вывод списка id товаров + имени товаров, id принадлежащего склада, их остатка на складах по имени города и/или имени продукта,
    // аналог запроса см. выше. SELECT прописывается отдельно в сервисных методах
    public Specification<ProductOnStockPlace> findByCityProduct(String city, String productN) {
        return Specification.where((root, query, criteriaBuilder) -> {
            Predicate result = null;
            Join<ProductOnStockPlace, Product> productJoin = root.join("product_id");
            Join<StockPlace, Stock> stock = productJoin
                    .join("stock_place_id")
                    .join("stock_id");
            Join<ProductOnStockPlace, Product> pr = root.join(ProductOnStockPlace_.)
                                   /*     CriteriaQuery<?> multiselect
                        = query.multiselect(criteriaBuilder.array(product.get("id"), stock.get("id"), root.get("quantity_product")));
criteriaBuilder.createQuery() */
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(city))) {
                result = criteriaBuilder.like(stock.get("city"), "%" + city + "%");
            }
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(productN))) {
                result = criteriaBuilder.like(productJoin.get("name"), "%" + productN + "%");
            }
            return result;
        });
    }
}
    /*public final EntityManager entityManager;

    public ProductOnStockPlaceSpecification(EntityManager entityManager) {
        this.entityManager = entityManager;
    }*/
   /*
    public Specification<ProductOnStockPlace> findByCityName2(String cityName) {
       CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductOnStockPlace> productOnStockPlaceCriteriaQuery = criteriaBuilder.createQuery(ProductOnStockPlace.class);
        Root<ProductOnStockPlace> root = productOnStockPlaceCriteriaQuery.from(ProductOnStockPlace.class);
        Join<ProductOnStockPlace, Product> productJoin = root.join("product_id");

        Join<Product, StockPlace> stockPlaceJoin = root.join("stock_place_id");
        Join<StockPlace, Stock> stockJoin = stockPlaceJoin.join("stock_id");

        Join<ProductOnStockPlace, StockPlace> testDoubleJoin = root.join("stock_place_id").join("stock_id");*/
/*        public Specification<Product> findByNameAndPrice(final String name, final BigDecimal minPrice, BigDecimal maxPrice, String price) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
            Root<Product> product = query.from(Product.class);
            query.select(product)
                    .where(criteriaBuilder.or(criteriaBuilder.like(product.get("name"), "%" + name + "%"),
                            criteriaBuilder.between(product.get("price"), minPrice, maxPrice)));

            criteriaBuilder.like(root.get("name"), "%" + name + "%");
        }*/
//}

/*CriteriaQuery<Pet> cq = cb.createQuery(Pet.class);

Root<Pet> pet = cq.from(Pet.class);
Join<Pet, Owner> owner = pet.join(Pet_.owners);
Joins can be chained together to navigate to related entities of the target
entity without having to create a Join<X, Y> instance for each join:

CriteriaQuery<Pet> cq = cb.createQuery(Pet.class);
Root<Pet> pet = cq.from(Pet.class);
Join<Owner, Address> address = cq.join(Pet_.owners).join(Owner_.addresses);*/

