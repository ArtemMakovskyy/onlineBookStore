package online.book.store.repository.book.spec;

import java.util.List;
import java.util.stream.Stream;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE_KEY = "price";

    @Override
    public String getKey() {
        return PRICE_KEY;
    }

    public Specification<Book> getSpecification(String[] params) {
        List<Double> doubles = Stream.of(params).map(Double::valueOf).sorted().toList();
        String priceFrom = String.valueOf(doubles.get(0));
        String priceTo = String.valueOf(doubles.get(doubles.size() - 1));
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.between(root.get(PRICE_KEY), priceFrom, priceTo);
    }
}
