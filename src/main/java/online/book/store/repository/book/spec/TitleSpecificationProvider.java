package online.book.store.repository.book.spec;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE_KEY = "title";

    @Override
    public String getKey() {
        return TITLE_KEY;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> titlePredicates = new ArrayList<>();
            for (String param : params) {
                titlePredicates.add(criteriaBuilder.like(root.get(TITLE_KEY), "%" + param + "%"));
            }
            return criteriaBuilder.or(titlePredicates.toArray(new Predicate[0]));
        };
    }
}
