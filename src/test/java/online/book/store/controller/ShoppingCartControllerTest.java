package online.book.store.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;

import lombok.SneakyThrows;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.cart.item.CartItemDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;
import online.book.store.model.Role;
import online.book.store.model.User;
import online.book.store.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    @Autowired
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShoppingCartServiceImpl shoppingCartService;

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
    @DisplayName("Get existent shoppingCart by valid user ID var.1")
    public void getShoppingCartById_Valid_ShouldReturnShoppingCartDto_var1() throws Exception {
        // given
        User user = getUser();
        ShoppingCartDto mockShoppingCartDto = getShoppingCartDto();
        ShoppingCartDto expectedShoppingCartDto = getShoppingCartDto();
        String jsonRequest = objectMapper.writeValueAsString(mockShoppingCartDto);

        when(shoppingCartService.getShoppingCartByUserId(user.getId())).thenReturn(expectedShoppingCartDto);

        // when, then
        final MvcResult result = mockMvc.perform(
                        get("/cart")
                                .with(user(user))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.cartItems[0].id").value(3))
                .andExpect(jsonPath("$.cartItems[0].bookId").value(3))
                .andExpect(jsonPath("$.cartItems[0].bookTitle").value("Sample Book 3"))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(5))
                .andExpect(jsonPath("$.cartItems[1].id").value(2))
                .andExpect(jsonPath("$.cartItems[1].bookId").value(2))
                .andExpect(jsonPath("$.cartItems[1].bookTitle").value("Sample Book 2"))
                .andExpect(jsonPath("$.cartItems[1].quantity").value(12))
                .andReturn();

        final ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);
        Assertions.assertEquals(expectedShoppingCartDto,actualShoppingCartDto);
        verify(shoppingCartService, times(1)).getShoppingCartByUserId(user.getId());
    }

    @Test
    @DisplayName("Get existent shoppingCart by valid user ID var.2")
    public void getShoppingCartById_Valid_ShouldReturnShoppingCartDto_var2() throws Exception {
        // given
        User user = getUser();
        ShoppingCartDto mockShoppingCartDto = getShoppingCartDto();
        ShoppingCartDto expectedShoppingCartDto = getShoppingCartDto();
        String jsonRequest = objectMapper.writeValueAsString(mockShoppingCartDto);

        when(shoppingCartService.getShoppingCartByUserId(user.getId())).thenReturn(expectedShoppingCartDto);

        // when
        final MvcResult result = mockMvc.perform(
                        get("/cart")
                                .with(user(user))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        final ShoppingCartDto actualShoppingCartDto =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(), ShoppingCartDto.class);
        Assertions.assertNotNull(actualShoppingCartDto);
        EqualsBuilder.reflectionEquals(expectedShoppingCartDto,actualShoppingCartDto);
        verify(shoppingCartService, times(1)).getShoppingCartByUserId(user.getId());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Add book to the existing Shopping Cart")
    public void addBookToTheShoppingCart_ValidAddBookToTheShoppingCartDto_ReturnDto() throws Exception {
        //given
        AddBookToTheShoppingCartDto requestAddBookToTheShoppingCartDto =
                new AddBookToTheShoppingCartDto()
                        .setBookId(1L)
                        .setQuantity(5);

        AddBookToTheShoppingCartDto expected = new AddBookToTheShoppingCartDto()
                .setBookId(requestAddBookToTheShoppingCartDto.getBookId())
                .setQuantity(requestAddBookToTheShoppingCartDto.getQuantity());

        final String jsonRequest = objectMapper.writeValueAsString(requestAddBookToTheShoppingCartDto);

        //when
        when(shoppingCartService.addBook(requestAddBookToTheShoppingCartDto, getUser().getId()))
                .thenReturn(expected);

        final MvcResult result = mockMvc.perform(post("/cart")
                        .with(user(getUser()))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final AddBookToTheShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AddBookToTheShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(shoppingCartService,times(1)).addBook(requestAddBookToTheShoppingCartDto, getUser().getId());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Update book quantity in the ShoppingCart")
    @Sql(scripts = "classpath:database/cart/item/insert-the-necessary-data-to-the-tables-for-correct-work-wit-cart-items.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-horror-category.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateBookQuantity_ValidBookQuantityDto_Success() throws Exception {
        long cartItemId = 2L;
        BookQuantityDto expected = new BookQuantityDto()
                .setQuantity(5);
        final String jsonRequest = objectMapper.writeValueAsString(expected);

        final MvcResult result = mockMvc.perform(
                        put("/cart/cart-items/{cartItemId}", cartItemId)
                                .with(user(getUser()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final BookQuantityDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookQuantityDto.class);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Delete existing CartItem by valid ID")
    public void delete_ValidCartItemId_Success() throws Exception {
        // Given
        long cartItemId = 1L;
        // When and Then
        mockMvc
                .perform(
                        delete("/cart/cart-items/{cartItemId}", cartItemId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1))
                .deleteCartItem(cartItemId);
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

    private User getUser() {
        Role userRole = new Role();
        userRole.setName(Role.RoleName.ROLE_USER);
        Role adminRole = new Role();
        adminRole.setName(Role.RoleName.ROLE_ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User user = new User();
        user.setId(2L);
        user.setRoles(roles);
        user.setFirstName("Some FirstName");
        user.setFirstName("Some LastName");
        user.setEmail("email@email.com");
        return user;
    }

    private ShoppingCartDto getShoppingCartDto() {
        return new ShoppingCartDto()
                .setId(2L)
                .setUserId(2L)
                .setCartItems(Set.of(
                        new CartItemDto().setId(3L).setBookId(3L).setBookTitle("Sample Book 3").setQuantity(5),
                        new CartItemDto().setId(2L).setBookId(2L).setBookTitle("Sample Book 2").setQuantity(12)
                ));
    }
}
