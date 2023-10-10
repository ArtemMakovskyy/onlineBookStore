package online.book.store.repository;

import java.util.List;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.model.Book;
import online.book.store.model.Category;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    private static final String CATEGORY_ADVENTURE = "Adventure";
    private static final String CATEGORY_COMEDY = "Comedy";
    private static final String CATEGORY_DRAMA = "Drama";
    private static final Long NON_EXISTENT_CATEGORY = 0L;
    private static final int EXCEPTED_ADVENTURES_QUANTITIES = 1;
    private static final int EXCEPTED_COMEDIES_QUANTITIES = 2;
    private static final int EXCEPTED_DRAMAS_QUANTITIES = 2;
    private static final int EXCEPTED_NON_EXISTENT_QUANTITIES = 0;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Find all books by existent book category ids")
    @Sql(scripts = {
            "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            "classpath:database/books/insert-books-into-table.sql",
            "classpath:database/categories/insert-three-categories-into-table.sql",
            "classpath:database/books/categories/insert-books-categories-into-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoriesId_ExistentCategoryIds_ReturnsNonEmptyLists() {
        final Pageable pageable = PageRequest.of(0, 10);

        final List<Book> actualAdventures
                = bookRepository.findAllByCategoriesId(
                findCategoryIdByName(CATEGORY_ADVENTURE), pageable);
        final List<Book> actualComedy
                = bookRepository.findAllByCategoriesId(
                findCategoryIdByName(CATEGORY_COMEDY), pageable);
        final List<Book> actualDrama
                = bookRepository.findAllByCategoriesId(
                findCategoryIdByName(CATEGORY_DRAMA), pageable);

        Assertions.assertEquals(EXCEPTED_ADVENTURES_QUANTITIES, actualAdventures.size());
        Assertions.assertEquals(EXCEPTED_COMEDIES_QUANTITIES, actualComedy.size());
        Assertions.assertEquals(EXCEPTED_DRAMAS_QUANTITIES, actualDrama.size());
    }

    @Test
    @DisplayName("Find all books by non-existent book category id")
    @Sql(scripts = {
            "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            "classpath:database/books/insert-books-into-table.sql",
            "classpath:database/categories/insert-three-categories-into-table.sql",
            "classpath:database/books/categories/insert-books-categories-into-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoriesId_NonExistentCategoryId_ReturnsEmptyLists() {
        final Pageable pageable = PageRequest.of(0, 10);

        final List<Book> allByNonExistentCategoriesId =
                bookRepository.findAllByCategoriesId(NON_EXISTENT_CATEGORY, pageable);

        Assertions.assertEquals(
                EXCEPTED_NON_EXISTENT_QUANTITIES, allByNonExistentCategoriesId.size());
    }

    private Long findCategoryIdByName(String categoryName) {
        final Category category = categoryRepository.findByName(categoryName).orElseThrow(
                () -> new EntityNotFoundException("Can't find category be name " + categoryName));
        return category.getId();
    }
}
