package online.book.store.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CartItemMapper;
import online.book.store.mapper.ShoppingCartMapper;
import online.book.store.model.CartItem;
import online.book.store.model.ShoppingCart;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.cart.item.CartItemRepository;
import online.book.store.repository.shopping.cart.ShoppingCartRepository;
import online.book.store.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCartDtoByUserId(Long id) {
        final ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Shopping Cart by id " + id));
        final ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        return shoppingCartDto;
    }

    @Override
    public AddBookToTheShoppingCartDto addBook(AddBookToTheShoppingCartDto createDto, Long userId) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookRepository.findById(createDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't get book by id "
                        + createDto.getBookId() + " and put it into CartItem")));
        cartItem.setQuantity(createDto.getQuantity());
        cartItem.setShoppingCart(shoppingCartRepository.findById(userId).get());
        final CartItem savedCartItem = cartItemRepository.save(cartItem);
        final ShoppingCart shoppingCart = shoppingCartRepository.findById(userId).get();
        shoppingCart.setCartItems(Set.of(savedCartItem));
        shoppingCartRepository.save(shoppingCart);
        return cartItemMapper.toCreateDto(savedCartItem);
    }

    @Override
    public BookQuantityDto updateBookQuantity(
            BookQuantityDto updateBookQuantityDto, Long cartItemId) {
        final CartItem cartItemFromDb = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find CartItem by id " + cartItemId));
        cartItemFromDb.setQuantity(updateBookQuantityDto.getQuantity());
        final CartItem updatedCartItem = cartItemRepository.save(cartItemFromDb);
        return cartItemMapper.toBookQuantityDto(updatedCartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
