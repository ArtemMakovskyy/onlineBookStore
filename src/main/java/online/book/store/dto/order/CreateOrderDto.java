package online.book.store.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDto {
    @NotNull(message = "Please fill in the details")
    private String shippingAddress;
}
