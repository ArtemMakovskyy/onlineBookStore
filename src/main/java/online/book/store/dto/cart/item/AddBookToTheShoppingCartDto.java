package online.book.store.dto.cart.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookToTheShoppingCartDto {
    private Long bookId;
    @NotNull(message = "Please enter quantity ")
    @Min(value = 0, message = "Invalid quantity, it cannot be less than zero")
    private int quantity;
}
