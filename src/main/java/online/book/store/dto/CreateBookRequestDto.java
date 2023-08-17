package online.book.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotBlank(message = "Title should not be blank")
    private String title;
    @NotBlank(message = "Author should not be blank")
    private String author;
    @NotBlank(message = "Isbn should not be blank")
    private String isbn;
    @NotNull(message = "Please enter price ")
    @Min(value = 0, message = "Invalid price, it cannot be less than zero")
    private BigDecimal price;
    private String description;
    private String coverImage;
}
