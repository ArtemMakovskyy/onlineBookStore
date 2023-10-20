package online.book.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryDto {
    @NotBlank(message = "name should not be blank")
    private String name;
    private String description;
}
