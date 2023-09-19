package online.book.store.dto.cart.item;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private String bookId;
    private String bookTitle;
    private int quantity;
}
