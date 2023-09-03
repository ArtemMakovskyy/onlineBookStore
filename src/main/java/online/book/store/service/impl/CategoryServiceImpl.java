package online.book.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.category.CategoryDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.BookMapper;
import online.book.store.mapper.CategoryMapper;
import online.book.store.model.Category;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.category.CategoryRepository;
import online.book.store.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find category by id " + id)));
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        final Category category = categoryMapper.toEntity(categoryDto);
        final Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        if (categoryRepository.existsById(id)) {
            Category category = new Category();
            category.setId(id);
            category.setName(dto.getName());
            category.setDescription(dto.getDescription());
            final Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toDto(savedCategory);
        }
        throw new EntityNotFoundException("Category by id " + id + " doesn't exist");
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
