package online.book.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    @NotBlank(message = "name should not be blank")
    private String name;
    private String description;
}
