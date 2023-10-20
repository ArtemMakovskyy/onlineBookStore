package online.book.store.dto.shopping.cart;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import online.book.store.dto.cart.item.CartItemDto;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
