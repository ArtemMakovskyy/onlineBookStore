package online.book.store.repository.book;

import java.util.List;
import java.util.Optional;
import online.book.store.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("FROM Book b join FETCH b.categories c "
            + "WHERE b.isDeleted = false AND  c.isDeleted = false ")
    List<Book> findAll();

    @Query("FROM Book b join FETCH b.categories c "
            + "WHERE b.id = :id AND b.isDeleted = false AND  c.isDeleted = false ")
    Optional<Book> getBookById(Long id);

    @Query("FROM Book b JOIN FETCH b.categories c "
            + "WHERE c.id = :categoryId")
    List<Book> findAllByCategoriesId(Long categoryId);
}
