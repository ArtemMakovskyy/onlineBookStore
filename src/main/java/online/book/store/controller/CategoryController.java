package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.category.CategoryDto;
import online.book.store.service.BookService;
import online.book.store.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints to managing categories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Creating a new category.",
            description = "Creating a new category with valid data. "
                    + "Name must be not blank and unique and in addition field description")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(
            @RequestBody @Valid CategoryDto createCategoryDto) {
        return categoryService.save(createCategoryDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Getting page available categories.",
            description = "Retrieve page with available categories. "
                    + "By default it is first page with 5 books, sorted ASC"
                    + "except those deleted using soft delete.")
    public Page<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,ASC") String sort) {
        return categoryService.findAll(page, size, sort);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Getting available category by id.",
            description = "Retrieve available category by id, "
                    + "if it has not been deleted with soft delete.")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Deleting category by id",
            description = "Soft deleting available category by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Updating category by id",
            description = "Updating available category by id, "
                    + "except those deleted using soft delete.")
    public CategoryDto updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryDto createCategoryDto) {
        return categoryService.update(id, createCategoryDto);
    }

    @GetMapping("/{categoryId}/books")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Getting page available books.",
            description = "Retrieve page with available books. "
                    + "By default it is first page with 5 books, sorted ASC"
                    + "except those deleted using soft delete.")
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "price,ASC") String sort) {
        return bookService.findAllByCategory(categoryId, page, size, sort);
    }
}
