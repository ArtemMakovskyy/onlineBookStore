package online.book.store.dto.shopping.cart;

import java.util.Set;
import lombok.Data;
import online.book.store.dto.cart.item.CartItemDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
