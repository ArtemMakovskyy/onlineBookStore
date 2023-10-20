package online.book.store.service.impl;

import lombok.RequiredArgsConstructor;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;
import online.book.store.exception.DataDuplicationException;
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
    public ShoppingCartDto getShoppingCartByUserId(Long id) {
        final ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Shopping Cart by id " + id));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public AddBookToTheShoppingCartDto addBook(AddBookToTheShoppingCartDto createDto, Long userId) {
        final ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart with id "
                        + userId + " doesn't exist"));
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            if (cartItem.getBook().getId().equals(createDto.getBookId())) {
                throw new DataDuplicationException("The Book with id " + createDto.getBookId()
                        + " already exists in this cartItem. You should update it.");
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setBook(bookRepository.findById(createDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't get book by id "
                        + createDto.getBookId() + " and put it into CartItem")));
        cartItem.setQuantity(createDto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        final CartItem savedCartItem = cartItemRepository.save(cartItem);
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
        if (!cartItemRepository.findById(cartItemId).isPresent()) {
            throw new EntityNotFoundException("CartItem by id " + cartItemId
                    + " does not exist. Cannot delete a non-existent CartItem");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
