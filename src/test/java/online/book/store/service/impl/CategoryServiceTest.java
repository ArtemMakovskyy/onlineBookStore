package online.book.store.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import online.book.store.dto.category.CategoryDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CategoryMapper;
import online.book.store.model.Category;
import online.book.store.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final int VIEW_PAGE_NUMBER = 0;
    private static final int ELEMENTS_QUANTITY_PER_PAGE = 10;
    private static final Long INVALID_NEGATIVE_ID = -1L;
    private static final Long INVALID_NON_EXISTING_ID = 1000L;
    private static final String SORT_PARAMETER = "name,ASC";
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
            Find categories in an empty category database
            """)
    public void findAll_ValidEmptyPage_ShouldNotReturnAnyCategories() {
        // Given
        Pageable pageable = PageRequest.of(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE,
                Sort.by(Sort.Order.asc(SORT_PARAMETER)));

        Mockito.when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        final PageImpl<CategoryDto> expected =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        //when
        final Page<CategoryDto> actual = categoryService.findAll(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETER);

        //then
        assertTrue(actual instanceof PageImpl);
        PageImpl<CategoryDto> actualPageImpl = (PageImpl<CategoryDto>) actual;
        assertEquals(expected.getTotalPages(), actualPageImpl.getTotalPages());
        verify(categoryRepository, times(1))
                .findAll(any(Pageable.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Find all existing categories
            """)
    public void findAll_ValidPage_ShouldReturnAllCategories() {
        // Given
        Pageable pageable = PageRequest.of(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE,
                Sort.by(Sort.Order.asc(SORT_PARAMETER)));

        Category fictionCategory = getCategoryFiction();
        Category novelCategory = getCategoryNovel();

        Page<Category> mockedPage = new PageImpl<>(
                Arrays.asList(fictionCategory, novelCategory), pageable, 2);
        Mockito.when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(mockedPage);

        PageImpl<CategoryDto> expected = new PageImpl<>(
                Arrays.asList(categoryToDto(fictionCategory),
                        categoryToDto(novelCategory)), pageable, 2);

        // When
        final Page<CategoryDto> actual = categoryService.findAll(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETER);

        // Then
        assertTrue(actual instanceof PageImpl);

        PageImpl<CategoryDto> actualPageImpl = (PageImpl<CategoryDto>) actual;
        assertEquals(expected.getTotalPages(), actualPageImpl.getTotalPages());
        verify(categoryRepository, times(1))
                .findAll(any(Pageable.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Find an existing category by valid ID")
    public void getById_WithValidCategoryId_ShouldReturnValidCategory() {
        //given
        Category category = getCategoryFiction();
        Long categoryId = category.getId();

        CategoryDto expected = categoryToDto(category);

        Mockito.when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category))
                .thenReturn(expected);

        //when
        CategoryDto actual = categoryService.getById(categoryId);

        // Then
        assertEquals(expected, actual);
        verify(categoryRepository, times(1))
                .findById(categoryId);
        verify(categoryMapper, times(1))
                .toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Make sure that an exception is thrown when passing a negative category identifier.
            """)
    public void getById_WithNegativeCategoryId_ShouldThrowException() {
        //given
        Mockito.when(categoryRepository.findById(INVALID_NEGATIVE_ID))
                .thenThrow(new EntityNotFoundException(
                        "Can't find category by id " + INVALID_NEGATIVE_ID));

        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> categoryService.getById(INVALID_NEGATIVE_ID));

        //then
        String expected = "Can't find category by id " + INVALID_NEGATIVE_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1))
                .findById(INVALID_NEGATIVE_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method is working with valid category")
    public void save_ValidCategoryDto_ReturnsCategoryDtoFromDb() {
        //given
        Category categoryFiction = getCategoryFiction();
        CategoryDto createFictionCategoryDtoToDb =
                categoryToDto(getCategoryFiction());
        CategoryDto getFictionCategoryDtoFromDb =
                categoryToDto(getCategoryFiction());

        when(categoryMapper.toEntity(createFictionCategoryDtoToDb))
                .thenReturn(categoryFiction);
        when(categoryRepository.save(categoryFiction))
                .thenReturn(categoryFiction);
        when(categoryMapper.toDto(categoryFiction))
                .thenReturn(createFictionCategoryDtoToDb);

        //when
        final CategoryDto savedCategoryDto = categoryService
                .save(createFictionCategoryDtoToDb);

        //then
        assertThat(createFictionCategoryDtoToDb).isEqualTo(getFictionCategoryDtoFromDb);
        verify(categoryMapper, times(1))
                .toEntity(createFictionCategoryDtoToDb);
        verify(categoryRepository, times(1))
                .save(categoryFiction);
        verify(categoryMapper, times(1))
                .toDto(categoryFiction);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update() method is working with valid category")
    public void update_ValidBook_BookHasBeenUpdated() {
        // Given
        final Category updatedCategoryFiction = getCategoryFiction();
        final CategoryDto expectedCategoryDto = categoryToDto(updatedCategoryFiction);

        when(categoryRepository.existsById(anyLong()))
                .thenReturn(true);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(updatedCategoryFiction);
        when(categoryMapper.toDto(updatedCategoryFiction))
                .thenReturn(expectedCategoryDto);

        // When
        CategoryDto actualCategoryDto = categoryService
                .update(anyLong(), expectedCategoryDto);

        // Then
        assertThat(actualCategoryDto.getName()).isEqualTo(expectedCategoryDto.getName());
        verify(categoryRepository, times(1))
                .existsById(anyLong());
        verify(categoryRepository, times(1))
                .save(any(Category.class));
        verify(categoryMapper, times(1))
                .toDto(updatedCategoryFiction);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify deleteById() method is working with valid category ID")
    public void deleteById_ValidId_CategoryHasBeenDeleted() {
        // Given
        Long categoryId = anyLong();

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // When
        categoryService.deleteById(categoryId);

        // Then
        verify(categoryRepository, times(1))
                .deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Make sure that an exception is thrown when passing a invalid category ID.")
    public void deleteById_WithInvalidCategoryId_ShouldThrowException() {
        //given
        Mockito.when(categoryRepository.findById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException(
                        "Category by id " + INVALID_NON_EXISTING_ID
                                + " does not exist. Cannot delete a non-existent category"));
        String expected = "Category by id " + INVALID_NON_EXISTING_ID
                + " does not exist. Cannot delete a non-existent category";

        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> categoryService.getById(INVALID_NON_EXISTING_ID));

        //then
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1))
                .findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    private Category getCategoryFiction() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("Fiction books");
        category.setDeleted(false);
        return category;
    }

    private Category getCategoryNovel() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Novel");
        category.setDescription("Novel books");
        category.setDeleted(false);
        return category;
    }

    private CategoryDto categoryToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }
}
