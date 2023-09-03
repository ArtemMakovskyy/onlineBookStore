package online.book.store.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.category.CategoryDto;
import online.book.store.service.BookService;
import online.book.store.service.CategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "Book management", description = "Endpoints to managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryDto createCategory(
            @RequestBody @Valid CategoryDto createCategoryDto) {
        return categoryService.save(createCategoryDto);
    }

    @GetMapping
    //    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CategoryDto> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    //    @PreAuthorize("hasRole('ROLE_USER')")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryDto updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryDto createCategoryDto) {
        return categoryService.update(id, createCategoryDto);
    }

    @DeleteMapping("/{id}")
    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    //    @PreAuthorize("hasRole('ROLE_USER')")
    public List<BookDtoWithoutCategoryIds> getAllBooksByCategory(@PathVariable Long id) {
        return bookService.findAllByCategory(id);
    }
}
