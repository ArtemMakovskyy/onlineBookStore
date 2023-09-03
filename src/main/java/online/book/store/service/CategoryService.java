package online.book.store.service;

import java.util.List;
import online.book.store.dto.category.CategoryDto;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
