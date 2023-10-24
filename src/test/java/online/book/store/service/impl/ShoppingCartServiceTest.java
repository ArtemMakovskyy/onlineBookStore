package online.book.store.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.cart.item.CartItemDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;
import online.book.store.exception.DataDuplicationException;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CartItemMapper;
import online.book.store.mapper.ShoppingCartMapper;
import online.book.store.model.Book;
import online.book.store.model.CartItem;
import online.book.store.model.Role;
import online.book.store.model.ShoppingCart;
import online.book.store.model.User;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.cart.item.CartItemRepository;
import online.book.store.repository.shopping.cart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    private static final Long INVALID_NON_EXISTING_ID = 1000L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Get ShoppingCart by valid user ID. Return ShoppingCartDto")
    public void getShoppingCartByUserId_ValidUserId_ReturnShoppingCartDto() {
        //given
        Long userId = anyLong();
        ShoppingCart shoppingCart = getShoppingCart();
        ShoppingCartDto expectedShoppingCartDto = mapShoppingCartToDto(shoppingCart);

        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedShoppingCartDto);

        //when
        final ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserId(userId);

        //then
        assertEquals(expectedShoppingCartDto, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Get ShoppingCart by invalid user ID. Throw EntityNotFoundException")
    public void getShoppingCartById_InvalidId_ThrowException() {
        //given
        when(shoppingCartRepository.findById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException(
                        "Can't find Shopping Cart by id " + INVALID_NON_EXISTING_ID));
        String expected = "Can't find Shopping Cart by id " + INVALID_NON_EXISTING_ID;

        //when
        EntityNotFoundException requestException =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.getShoppingCartByUserId(
                                INVALID_NON_EXISTING_ID));
        final String actual = requestException.getMessage();

        //then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, timeout(1)).findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Add existing book to the existing ShoppingCart. Return dto")
    public void addBook_ValidBookAndShoppingCart_Success() {
        //given
        AddBookToTheShoppingCartDto expectedCreateDto =
                new AddBookToTheShoppingCartDto();
        int newQuantity = new Random().nextInt(100) + 1;
        expectedCreateDto.setBookId(getBookA().getId());
        expectedCreateDto.setQuantity(newQuantity);

        Long userId = getUserWithUserRole().getId();
        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(getShoppingCart()));
        when(bookRepository.findById(getBookA().getId())).thenReturn(Optional.of(getBookA()));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(getCartItem());
        when(cartItemMapper.toCreateDto(any(CartItem.class))).thenReturn(expectedCreateDto);

        //when
        AddBookToTheShoppingCartDto actual = shoppingCartService.addBook(expectedCreateDto, userId);

        //then
        assertEquals(expectedCreateDto, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(getBookA().getId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verifyNoMoreInteractions(shoppingCartRepository, bookRepository, cartItemRepository);
    }

    @Test
    @DisplayName("""
            Add existing book to the invalid ShoppingCart Id. Throw EntityNotFoundException
            """)
    public void addBook_InvalidShoppingCartId_ThrowException() {
        //given
        String expectedExceptionMessage = "ShoppingCart with id "
                + INVALID_NON_EXISTING_ID + " doesn't exist";
        when(shoppingCartRepository.findById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException("ShoppingCart with id "
                        + INVALID_NON_EXISTING_ID + " doesn't exist"));

        //when
        final EntityNotFoundException actualException = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addBook(
                        any(AddBookToTheShoppingCartDto.class), INVALID_NON_EXISTING_ID));
        final String actualExceptionMessage = actualException.getMessage();

        //then
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
        verify(shoppingCartRepository, times(1)).findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Add the duplicate book to your existing shopping cart. Throw DataDuplicationException
            """)
    public void addBook_BookDuplicate_ThrowException() {
        //given
        AddBookToTheShoppingCartDto duplicateBookDto = new AddBookToTheShoppingCartDto();
        duplicateBookDto.setBookId(getBookA().getId());
        int newQuantity = new Random().nextInt(100) + 1;
        duplicateBookDto.setQuantity(newQuantity);

        ShoppingCart existedShoppingCart = getShoppingCart();
        existedShoppingCart.setCartItems(Set.of(getCartItem()));

        Long existingUserId = getUserWithUserRole().getId();
        when(shoppingCartRepository.findById(existingUserId))
                .thenReturn(Optional.of(existedShoppingCart));

        //when
        final DataDuplicationException actualException = assertThrows(
                DataDuplicationException.class, () -> shoppingCartService.addBook(
                        duplicateBookDto, existingUserId));
        final String actualExceptionMessage = actualException.getMessage();

        //then
        String expectedDataDuplicationExceptionMessage = "The Book with id " + getBookA().getId()
                + " already exists in this cartItem. You should update it.";
        assertEquals(expectedDataDuplicationExceptionMessage,actualExceptionMessage);
        verify(shoppingCartRepository,times(1)).findById(existingUserId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Change valid book quantity in the ShoppingCart. Return BookQuantityDto.
            """)
    public void updateBookQuantity_ValidDate_Success() {
        //given
        int newQuantity = new Random().nextInt(100) + 1;
        CartItem cartItemFromDb = getCartItem();
        CartItem updatedCartItemFromDb = getCartItem();
        updatedCartItemFromDb.setQuantity(newQuantity);

        when(cartItemRepository.findById(cartItemFromDb.getId()))
                .thenReturn(Optional.of(cartItemFromDb));
        when(cartItemRepository.save(updatedCartItemFromDb))
                .thenReturn(updatedCartItemFromDb);
        BookQuantityDto expectedUpdateBookQuantityDto =
                new BookQuantityDto().setQuantity(newQuantity);
        when(cartItemMapper.toBookQuantityDto(updatedCartItemFromDb))
                .thenReturn(expectedUpdateBookQuantityDto);

        //when
        final BookQuantityDto actual = shoppingCartService.updateBookQuantity(
                expectedUpdateBookQuantityDto, getCartItem().getId());

        //then
        assertEquals(expectedUpdateBookQuantityDto, actual);
        verify(cartItemRepository, times(1)).findById(cartItemFromDb.getId());
        verify(cartItemRepository, times(1)).save(updatedCartItemFromDb);
        verify(cartItemMapper, times(1)).toBookQuantityDto(updatedCartItemFromDb);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("""
            Change book quantity by negative in the ShoppingCart. Throw an Exception.
            """)
    public void updateBookQuantity_InvalidCartItemId_ThrowException() {
        //given
        String expected = "Can't get book by id "
                + INVALID_NON_EXISTING_ID + " and put it into CartItem";
        when(cartItemRepository.findById(INVALID_NON_EXISTING_ID)).thenThrow(
                new EntityNotFoundException("Can't get book by id "
                        + INVALID_NON_EXISTING_ID + " and put it into CartItem"));

        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.deleteCartItem(INVALID_NON_EXISTING_ID));
        final String actual = exception.getMessage();

        //then
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Delete CartItem by valid Id.")
    public void deleteCartItem_ValidId_Success() {
        //given
        Long cartItemId = anyLong();
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(getCartItem()));
        //when
        cartItemRepository.findById(cartItemId);

        //then
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Delete CartItem by invalid Id. Throw an Exception")
    public void deleteCartItem_InvalidId_ThrowEntityNotFoundException() {
        //given
        String expected = "CartItem by id " + INVALID_NON_EXISTING_ID
                + " does not exist. Cannot delete a non-existent CartItem";
        when(cartItemRepository.findById(INVALID_NON_EXISTING_ID)).thenThrow(
                new EntityNotFoundException("CartItem by id " + INVALID_NON_EXISTING_ID
                        + " does not exist. Cannot delete a non-existent CartItem"));
        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.deleteCartItem(INVALID_NON_EXISTING_ID));
        final String actual = exception.getMessage();

        //then
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(cartItemRepository);
    }

    private ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(getUserWithUserRole());
        shoppingCart.setCartItems(Set.of());
        shoppingCart.setDeleted(false);
        return shoppingCart;
    }

    private User getUserWithUserRole() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email");
        user.setPassword("password");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setShippingAddress("shopping cart address");
        user.setRoles(Set.of(getUserRole()));
        user.setDeleted(false);
        return user;
    }

    private Role getUserRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_USER);
        role.setDeleted(false);
        return role;
    }

    private CartItem getCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(new ShoppingCart());
        cartItem.setBook(getBookA());
        cartItem.setQuantity(1);
        cartItem.setDeleted(false);
        return cartItem;
    }

    private Book getBookA() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book A");
        book.setAuthor("Author A");
        book.setIsbn("978123456789b1");
        book.setPrice(BigDecimal.valueOf(22.22));
        book.setDescription("This is a sample book1 description.");
        book.setCoverImage("http://example.com/coverb.jpg");
        book.setDeleted(false);
        return book;
    }

    private ShoppingCartDto mapShoppingCartToDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCartDto.getUserId());
        shoppingCartDto.setCartItems(Set.of(new CartItemDto()));
        return shoppingCartDto;
    }
}
