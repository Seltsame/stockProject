package ru.rocketscience.test.stock;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

@Configuration
public class StockSpecification {

    public Specification<Stock> findByNameAndCity(final String name, final String city) {
        return (root, query, criteriaBuilder) -> {
            //StringUtils.isEmpty():
            //StringUtils.trimAllWhitespace():
            Predicate result = null;
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(name))) {
                result = criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
            if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(city))) {
                result = criteriaBuilder.like(root.get("city"), "%" + city + "%");
            }
            return result;
        };
    }

}
