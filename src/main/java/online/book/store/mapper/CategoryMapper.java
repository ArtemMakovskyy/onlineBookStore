package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.category.CategoryDto;
import online.book.store.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
