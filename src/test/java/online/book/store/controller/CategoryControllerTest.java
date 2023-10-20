package online.book.store.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.category.CategoryDto;
import online.book.store.service.impl.BookServiceImpl;
import online.book.store.service.impl.CategoryServiceImpl;
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
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final int VIEW_PAGE_NUMBER = 0;
    private static final int ELEMENTS_QUANTITY_PER_PAGE = 10;
    private static final String SORT_PARAMETERS = "price,ASC";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryServiceImpl categoryService;
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
    @Sql(scripts = "classpath:database/categories/delete-horror-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new category with valid data")
    void createCategory_ValidRequestDto_Success() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto()
                .setName("Horror (test category)")
                .setDescription("Horror movies are designed to scare or frighten viewers.");

        CategoryDto expected = new CategoryDto()
                .setName(categoryDto.getName())
                .setDescription(categoryDto.getDescription());

        //when
        when(categoryService.save(any(CategoryDto.class))).thenReturn(expected);

        mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.description").value(expected.getDescription()));

        //then
        verify(categoryService, times(1)).save(any(CategoryDto.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Find all existent categories")
    void getAllCategories_ShouldReturnAllCategories_Success3() throws Exception {
        // given
        List<CategoryDto> expectedCategories = Arrays.asList(
                new CategoryDto().setName("category 1").setDescription("description 1"),
                new CategoryDto().setName("category 2").setDescription("description 2"),
                new CategoryDto().setName("category 3").setDescription("description 3")
        );
        Page<CategoryDto> expectedPage = new PageImpl<>(expectedCategories);

        // when
        when(categoryService.findAll(0, 5, "name,ASC")).thenReturn(expectedPage);

        // then
        mockMvc.perform(
                        get("/categories")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sort", "name,ASC")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name", is("category 1")))
                .andExpect(jsonPath("$.content[1].name", is("category 2")))
                .andExpect(jsonPath("$.content[2].name", is("category 3")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get existent category by valid ID")
    public void getCategoryById_ValidCategoryId_Success() throws Exception {
        // Given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto()
                .setName("Test Category")
                .setDescription("Test description");

        when(categoryService.getById(categoryId)).thenReturn(categoryDto);

        // When and Then
        mockMvc.perform(
                get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryDto.getName()))
                .andExpect(jsonPath("$.description").value(categoryDto.getDescription()));

        verify(categoryService, times(1)).getById(categoryId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete existent category by valid ID")
    void deleteCategory_ValidCategoryId_Success() throws Exception {
        // Given
        long categoryId = 1L;

        // When and Then
        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteById(categoryId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update existent category by new valid category")
    void updateCategory_ValidData_Success() throws Exception {
        // Given
        long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setName("Updated Category");
        updateCategoryDto.setDescription("Updated Category Description");

        when(categoryService.update(eq(categoryId), any(CategoryDto.class)))
                .thenReturn(updateCategoryDto);

        // When and Then
        mockMvc.perform(
                        put("/categories/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Category")))
                .andExpect(jsonPath("$.description", is("Updated Category Description")));

        verify(categoryService, times(1)).update(eq(categoryId), any(CategoryDto.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get page of books by category ID")
    void getBooksByCategoryId_ValidCategoryAndBooks_ShouldReturnPageWithBooks() throws Exception {
        // Given
        long categoryId = 1L;
        Page<BookDtoWithoutCategoryIds> bookPage = new PageImpl<>(Collections.emptyList());
        when(bookService.findAllByCategory(
                categoryId, VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS))
                .thenReturn(bookPage);

        // When and Then
        mockMvc.perform(get("/categories/{categoryId}/books", categoryId)
                        .param("page", String.valueOf(VIEW_PAGE_NUMBER))
                        .param("size", String.valueOf(ELEMENTS_QUANTITY_PER_PAGE))
                        .param("sort", SORT_PARAMETERS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookPage)));

        verify(bookService, times(1)).findAllByCategory(
                categoryId, VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS);
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
}
