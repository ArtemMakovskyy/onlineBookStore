package online.book.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = "Price should be positive")
    private BigDecimal price;
    private String description;
    private String coverImage;
}
