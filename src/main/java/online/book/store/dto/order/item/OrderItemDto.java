package online.book.store.dto.order.item;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private String bookId;
    private int quantity;
}
