package online.book.store.repository.book;

import java.util.Optional;
import online.book.store.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends
        JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {
    Optional<Book> getBookById(Long id);

}