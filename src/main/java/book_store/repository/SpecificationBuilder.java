package book_store.repository;

import book_store.dto.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder <T>{
    Specification<T> build(BookSearchParameters bookSearchParameters);
}
