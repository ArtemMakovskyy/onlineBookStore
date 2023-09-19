package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.cart.item.CartItemDto;
import online.book.store.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    AddBookToTheShoppingCartDto toCreateDto(CartItem cartItem);

    BookQuantityDto toBookQuantityDto(CartItem cartItem);
}
