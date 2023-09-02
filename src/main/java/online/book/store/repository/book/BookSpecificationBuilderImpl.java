package online.book.store.repository.book;

import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationBuilder;
import online.book.store.repository.SpecificationProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilderImpl implements SpecificationBuilder<Book> {
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String ISBN_KEY = "isbn";
    private static final String PRICE_KEY = "price";

    @Autowired
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.title() != null && searchParameters.title().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(TITLE_KEY)
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.author() != null && searchParameters.author().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(AUTHOR_KEY)
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && searchParameters.isbn().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(ISBN_KEY)
                    .getSpecification(searchParameters.isbn()));
        }
        if (searchParameters.price() != null && searchParameters.price().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(PRICE_KEY)
                    .getSpecification(searchParameters.price()));
        }
        return spec;
    }
}
