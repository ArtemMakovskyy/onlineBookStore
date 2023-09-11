package online.book.store.dto.cart.item;

import lombok.Data;

@Data
public class AddBookToTheShoppingCartDto {
    private Long bookId;
    private int quantity;
}
