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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryDto> findAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        final List<CategoryDto> categoryDtos = categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
        return new PageImpl<>(categoryDtos,pageable,categoryDtos.stream().count());
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

    private Sort.Order parseSortOrder(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        String direction = parts[1].toUpperCase();
        return new Sort.Order(Sort.Direction.valueOf(direction), property);
    }
}
