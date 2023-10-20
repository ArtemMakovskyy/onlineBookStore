package online.book.store.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.service.impl.BookServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private static final int VIEW_PAGE_NUMBER = 0;
    private static final int ELEMENTS_QUANTITY_PER_PAGE = 10;
    private static final String SORT_PARAMETERS = "price,ASC";

    @Autowired
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext)
            throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/delete-items-from-all-tables-book-linked.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new book with valid requestDto")
    void createBook_ValidCreateBookRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto createBookRequestDto = getFirstCreateBookRequest();
        BookDto expectedBookDto = getFirstBookDto();
        when(bookService.save(createBookRequestDto)).thenReturn(expectedBookDto);

        // When and Then
        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(createBookRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookDto)));

        verify(bookService, timeout(100).times(1)).save(createBookRequestDto);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/delete-items-from-all-tables-book-linked.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all available books on page")
    void getAllBooks_ValidData_Success() throws Exception {
        // given
        List<BookDto> expectedBooks = Arrays.asList(
                getFirstBookDto(),
                getSecondBookDto(),
                getThirdBookDto()
        );
        Page<BookDto> expectedPage = new PageImpl<>(expectedBooks);

        when(bookService.findAll(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS))
                .thenReturn(expectedPage);

        // when then
        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "price,ASC")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content", hasSize(expectedBooks.size())),
                        jsonPath("$.content[0].title", is("Book sample")),
                        jsonPath("$.content[1].title", is("Book sample 2")),
                        jsonPath("$.content[2].title", is("Book sample 3")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get available book by valid ID")
    void getBookById_ValidId_ShouldReturnBookDto() throws Exception {
        // Given
        BookDto expectedBookDto = getFirstBookDto();
        Long bookId = expectedBookDto.getId();

        when(bookService.getBookById(bookId)).thenReturn(expectedBookDto);

        // When and Then
        mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookDto)));

        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete existing book by valid ID")
    void deleteBookById_ExistingBook_Success() throws Exception {
        // Given
        Long existingBookId = anyLong();

        // When and Then
        mockMvc.perform(delete("/books/{id}", existingBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(existingBookId);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/delete-items-from-all-tables-book-linked.sql"));
        }
    }

    private final BookDto getFirstBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Book sample");
        bookDto.setAuthor("Author A");
        bookDto.setIsbn("978-3-16-148410-1");
        bookDto.setPrice(new BigDecimal("19.99"));
        bookDto.setDescription("Book description 1");
        bookDto.setCoverImage("cover.jpg");
        bookDto.setCategoriesIds(Set.of(1L, 2L, 3L));
        return bookDto;
    }

    private final BookDto getSecondBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(2L);
        bookDto.setTitle("Book sample 2");
        bookDto.setAuthor("Author B");
        bookDto.setIsbn("978-3-16-148410-2");
        bookDto.setPrice(new BigDecimal("29.99"));
        bookDto.setDescription("Book description 2");
        bookDto.setCoverImage("cover2.jpg");
        bookDto.setCategoriesIds(Set.of(2L, 3L));
        return bookDto;
    }

    private final BookDto getThirdBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(3L);
        bookDto.setTitle("Book sample 3");
        bookDto.setAuthor("Author C");
        bookDto.setIsbn("978-3-16-148410-3");
        bookDto.setPrice(new BigDecimal("39.99"));
        bookDto.setDescription("Book description 3");
        bookDto.setCoverImage("cover3.jpg");
        bookDto.setCategoriesIds(Set.of(3L));
        return bookDto;
    }

    private final CreateBookRequestDto getFirstCreateBookRequest() {
        CreateBookRequestDto bookDto = new CreateBookRequestDto();
        bookDto.setTitle("Book sample");
        bookDto.setAuthor("Author A");
        bookDto.setIsbn("978-3-16-148410-1");
        bookDto.setPrice(new BigDecimal("19.99"));
        bookDto.setDescription("Book description 1");
        bookDto.setCoverImage("cover.jpg");
        bookDto.setCategories(Set.of(1L, 2L, 3L));
        return bookDto;
    }
}
