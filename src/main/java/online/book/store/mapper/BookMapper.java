package online.book.store.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import online.book.store.config.MapperConfig;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.model.Book;
import online.book.store.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto bookDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        final Set<Long> categoryIds = book.getCategories().stream()
                .map(category -> category.getId())
                .collect(Collectors.toSet());
        bookDto.setCategoriesIds(categoryIds);
    }

    @AfterMapping
    default void setCategoriesIntoBook(@MappingTarget Book book, CreateBookRequestDto createDto) {
            final Set<Category> categories = new HashSet<>();
            for (Long id : createDto.getCategories()) {
                Category category = new Category(id);
                categories.add(category);
            }
            book.setCategories(categories);
    }
}
