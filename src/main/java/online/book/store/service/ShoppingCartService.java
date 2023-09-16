package online.book.store.service;

import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartDtoByUserId(Long id);

    AddBookToTheShoppingCartDto addBook(AddBookToTheShoppingCartDto createDto, Long userId);

    BookQuantityDto updateBookQuantity(BookQuantityDto updateBookQuantityDto, Long cartItemId);

    void deleteCartItem(Long cartItemId);
}
