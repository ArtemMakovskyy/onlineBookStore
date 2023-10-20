package online.book.store.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.BookMapper;
import online.book.store.model.Book;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.book.BookSpecificationBuilderImpl;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final int VIEW_PAGE_NUMBER = 0;
    private static final int ELEMENTS_QUANTITY_PER_PAGE = 10;
    private static final Long INVALID_NEGATIVE_ID = -1L;
    private static final Long INVALID_NON_EXISTING_ID = 1000L;
    private static final String SORT_PARAMETERS = "title,ASC";
    private static final String SORT_FIELD = "title";

    @Mock
    private BookSpecificationBuilderImpl bookSpecificationBuilder;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @DisplayName("Find existing book by valid id and return bookDto")
    @Test
    void getBookById_ValidBookId_ReturnBookDto() {
        //given
        Long bookId = anyLong();
        Book book = getBookA();
        BookDto expectedBookDto = bookToDto(book);

        //when
        when(bookRepository.getBookById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);
        BookDto actual = bookService.getBookById(bookId);

        //then
        assertEquals(expectedBookDto, actual);
        verify(bookRepository, times(1)).getBookById(bookId);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @DisplayName("""
            Find book by negative book ID and throw EntityNotFoundException
            """)
    @Test
    void getBookById_InvalidNegativeBookId_ThrowEntityNotFoundException() {
        //given
        when(bookRepository.getBookById(INVALID_NEGATIVE_ID))
                .thenThrow(new EntityNotFoundException("Can't get book by id "
                        + INVALID_NEGATIVE_ID));

        //when
        final EntityNotFoundException actualException =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.getBookById(INVALID_NEGATIVE_ID));

        //then
        assertEquals("Can't get book by id "
                + INVALID_NEGATIVE_ID, actualException.getMessage());
        verify(bookRepository, times(1))
                .getBookById(INVALID_NEGATIVE_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("""
            Find a book by a non-existent book ID and throw an EntityNotFoundException.
            """)
    @Test
    void getBookById_InvalidNonExistentBookId_ThrowEntityNotFoundException() {
        //given
        when(bookRepository.getBookById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException("Can't get book by id "
                        + INVALID_NON_EXISTING_ID));

        //when
        final EntityNotFoundException actualException =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.getBookById(INVALID_NON_EXISTING_ID));

        //then
        assertEquals("Can't get book by id "
                + INVALID_NON_EXISTING_ID, actualException.getMessage());
        verify(bookRepository, times(1))
                .getBookById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Find all pages with Page state")
    public void findAll_ValidPage_ShouldReturnAllPages() {
        // Given
        Pageable pageable = PageRequest.of(VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE,
                Sort.by(Sort.Order.asc(SORT_PARAMETERS)));
        Page<Book> mockedPage = new PageImpl<>(
                Arrays.asList(getBookA(), getBookB()), pageable, 2);

        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(mockedPage);
        PageImpl<BookDto> expected = new PageImpl<>(
                Arrays.asList(bookToDto(getBookA()), bookToDto(getBookB())), pageable, 2);

        // When
        final Page<BookDto> actual = bookService.findAll(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS);

        // Then
        assertTrue(actual instanceof PageImpl);
        assertNotNull(actual);
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
        verify(bookRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Can't find any book pages")
    public void findAll_ValidPage_ShouldNotReturnAnyPages() {
        // Given
        Pageable pageable = PageRequest.of(VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE,
                Sort.by(Sort.Order.asc(SORT_PARAMETERS)));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        final PageImpl<BookDto> expected =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        //when
        Page<BookDto> actual = bookService.findAll(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS);

        //then
        assertTrue(actual instanceof PageImpl);
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        assertEquals(expected.getSize(), actual.getSize());
        verify(bookRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Verify deleteById() method is working with valid book ID")
    void deleteById_ValidId_CategoryHasBeenDeleted() {
        // Given
        Long categoryId = anyLong();

        when(bookRepository.existsById(categoryId)).thenReturn(true);

        // when
        bookService.deleteById(categoryId);

        // then
        verify(bookRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Make sure that an exception is thrown when passing a invalid book ID.")
    public void deleteById_WithInvalidBookId_ShouldThrowException() {
        //given
        Mockito.when(bookRepository.existsById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException(
                        "Book by id " + INVALID_NON_EXISTING_ID
                                + " does not exist. cannot delete a non-existent book"));
        String expected = "Book by id " + INVALID_NON_EXISTING_ID
                + " does not exist. cannot delete a non-existent book";

        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.deleteById(INVALID_NON_EXISTING_ID));

        //then
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1))
                .existsById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("""
            Updating existing book by valid book. Should return bookDto
            """)
    @Test
    void update_VerifyExistingBook_ReturnUpdatedBookDto() {
        //given
        Book updatedBook = getBookA();
        BookDto expected = bookToDto(updatedBook);

        Long existingBookId = anyLong();
        CreateBookRequestDto createBookRequestDto = toCreateBookRequestDto(updatedBook);

        when(bookRepository.existsById(existingBookId)).thenReturn(true);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toEntity(createBookRequestDto)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expected);

        //when
        BookDto actual = bookService.update(existingBookId, createBookRequestDto);

        //then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).existsById(existingBookId);
        verify(bookRepository, times(1)).save(updatedBook);
        verify(bookMapper, times(1)).toEntity(createBookRequestDto);
        verify(bookMapper, times(1)).toDto(updatedBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @DisplayName("""
            Updating a non-existing book should throw EntityNotFoundException
            """)
    @Test
    void update_InvalidNonExistingBook_ThrowEntityNotFoundException() {
        //given
        CreateBookRequestDto requestDto = toCreateBookRequestDto(getBookA());
        when(bookRepository.existsById(INVALID_NON_EXISTING_ID)).thenReturn(false);

        //when
        EntityNotFoundException actualException =
                assertThrows(EntityNotFoundException.class, () -> {
                    bookService.update(INVALID_NON_EXISTING_ID, requestDto);
                });

        //then
        assertEquals("Book by id " + INVALID_NON_EXISTING_ID
                + " does not exist", actualException.getMessage());
        verify(bookRepository, times(1))
                .existsById(INVALID_NON_EXISTING_ID);
        verify(bookRepository, never()).save(any(Book.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search books by valid title, should return books with matching title")
    void search_ValidTitle_ReturnMatchingBooks() {
        //given
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"Book A"},
                new String[]{},
                new String[]{},
                new String[]{});

        Book bookA = getBookA();
        BookDto expectedBookDto = bookToDto(bookA);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(SORT_FIELD).ascending());
        Page<Book> bookPage = new PageImpl<>(List.of(bookA), pageable, 1);
        Specification<Book> bookSpecification = mock(Specification.class);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookA)).thenReturn(expectedBookDto);

        //when
        final Page<BookDto> actual = bookService.search(searchParameters, pageable);

        //then
        assertEquals(searchParameters.title()[0], actual.stream().toList().get(0).getTitle());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getContent().size());
        assertEquals(expectedBookDto, actual.getContent().get(0));
        verify(bookSpecificationBuilder, times(1)).build(searchParameters);
        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper, times(1)).toDto(bookA);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @DisplayName("Search books by valid Author, should return books with matching Author")
    @Test
    void search_ValidAuthor_ReturnMatchingBooks() {
        //given
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{},
                new String[]{"Author A"},
                new String[]{},
                new String[]{});

        Book book = getBookA();
        BookDto expectedBookDto = bookToDto(book);
        Pageable pageable = PageRequest.of(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, Sort.by(SORT_FIELD).ascending());
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
        Specification<Book> bookSpecification = mock(Specification.class);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        //when
        final Page<BookDto> actual = bookService.search(searchParameters, pageable);

        //then
        assertEquals(searchParameters.author()[0], actual.stream().toList().get(0).getAuthor());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getContent().size());
        assertEquals(expectedBookDto, actual.getContent().get(0));
        verify(bookSpecificationBuilder, times(1)).build(searchParameters);
        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Search books by valid ISBN, should return books with matching ISBN")
    void search_ValidIsbn_ReturnMatchingBooks() {
        //given
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{},
                new String[]{},
                new String[]{"978123456789b1"},
                new String[]{});

        Book book = getBookA();
        BookDto expectedBookDto = bookToDto(book);
        Pageable pageable = PageRequest.of(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, Sort.by(SORT_FIELD).ascending());
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
        Specification<Book> bookSpecification = mock(Specification.class);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        //when
        final Page<BookDto> actual = bookService.search(searchParameters, pageable);

        //then
        assertEquals(searchParameters.isbn()[0], actual.stream().toList().get(0).getIsbn());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getContent().size());
        assertEquals(expectedBookDto, actual.getContent().get(0));
        verify(bookSpecificationBuilder, times(1)).build(searchParameters);
        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Search books by valid price, should return two books with matching price")
    void search_ValidPrice_ReturnMatchingBooks() {
        //given
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{},
                new String[]{},
                new String[]{},
                new String[]{"22,24"});

        Book bookA = getBookA();
        Book bookB = getBookB();
        BookDto expectedBookDtoA = bookToDto(bookA);
        BookDto expectedBookDtoB = bookToDto(bookB);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(SORT_FIELD).ascending());
        Page<Book> bookPage = new PageImpl<>(List.of(bookA, bookB), pageable, 2);
        Specification<Book> bookSpecification = mock(Specification.class);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookA)).thenReturn(expectedBookDtoA);
        when(bookMapper.toDto(bookB)).thenReturn(expectedBookDtoB);

        //when
        final Page<BookDto> actual = bookService.search(searchParameters, pageable);

        //then
        Assertions.assertEquals(1, actual.getTotalPages());
        Assertions.assertEquals(2, actual.getContent().size());
        Assertions.assertEquals(expectedBookDtoA, actual.getContent().get(0));
        Assertions.assertEquals(expectedBookDtoB, actual.getContent().get(1));
        verify(bookSpecificationBuilder, times(1)).build(searchParameters);
        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper, times(1)).toDto(bookA);
        verifyNoMoreInteractions(bookRepository, bookSpecificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Doesn't find books by category ID in the clean database")
    public void findAllByCategory_ValidPage_ShouldNotReturnAnyBooks() {
        //given
        Long categoryId = 1L;

        when(bookRepository.findAllByCategoriesId(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        //when
        final Page<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategory(
                categoryId, VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS);

        //then
        assertEquals(0, actual.getTotalPages());
        assertEquals(0, actual.getTotalElements());
        assertEquals(0, actual.getNumberOfElements());
        assertEquals(0, actual.getContent().size());
        assertTrue(actual.isEmpty());
        verify(bookRepository, times(1))
                .findAllByCategoriesId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("""
            Find books by category with valid page
            """)
    public void findAllByCategory_ValidPage_ShouldReturnAvailableBooks() {
        // Given
        Long categoryId = 1L;

        Pageable pageable = PageRequest.of(
                VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE,
                Sort.by(Sort.Order.asc(SORT_PARAMETERS)));
        Book book1 = getBookA();
        Book book2 = getBookB();

        when(bookRepository.findAllByCategoriesId(categoryId, pageable))
                .thenReturn(Arrays.asList(book1, book2));
        when(bookRepository.count()).thenReturn(2L);
        final List<Book> expectedBooks =
                bookRepository.findAllByCategoriesId(categoryId, pageable);

        // When
        final Page<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategory(
                categoryId, VIEW_PAGE_NUMBER, ELEMENTS_QUANTITY_PER_PAGE, SORT_PARAMETERS);

        // Then
        assertNotNull(actual);
        assertEquals(expectedBooks.size(), actual.getTotalElements());
        assertEquals(1, actual.getTotalPages());
        assertEquals(expectedBooks.size(), actual.getTotalElements());
        verify(bookRepository, times(1))
                .findAllByCategoriesId(categoryId, pageable);
        verify(bookRepository, times(1)).count();
        final List<BookDtoWithoutCategoryIds> content = actual.getContent();
    }

    @Test
    void testSaveBook() {
        // given
        Book book = getBookA();
        CreateBookRequestDto requestDto = toCreateBookRequestDto(getBookA());
        BookDto expected = bookToDto(book);

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        // when
        BookDto actual = bookService.save(requestDto);

        // then
        assertEquals(expected, actual);
        verify(bookMapper, times(1)).toEntity(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    private Book getWrongBook() {
        Book wrongBook = new Book();
        wrongBook.setId(3L);
        wrongBook.setTitle("Wrong Book 1");
        wrongBook.setAuthor("Author A");
        wrongBook.setIsbn("w978123456789w");
        wrongBook.setPrice(BigDecimal.valueOf(11.11));
        wrongBook.setDescription("This is a sample book1 description.");
        wrongBook.setCoverImage("http://example.com/coverw.jpg");
        wrongBook.setDeleted(false);
        return wrongBook;
    }

    private Book getBookA() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book A");
        book.setAuthor("Author A");
        book.setIsbn("978123456789b1");
        book.setPrice(BigDecimal.valueOf(22.22));
        book.setDescription("This is a sample book1 description.");
        book.setCoverImage("http://example.com/coverb.jpg");
        book.setDeleted(false);
        return book;
    }

    private Book getBookB() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Book B");
        book.setAuthor("Author B");
        book.setIsbn("978123456789b2");
        book.setPrice(BigDecimal.valueOf(22.30));
        book.setDescription("This is a sample book2 description.");
        book.setCoverImage("http://example.com/coverb.jpg");
        book.setDeleted(false);
        return book;
    }

    private BookDto bookToDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        return bookDto;
    }

    private CreateBookRequestDto toCreateBookRequestDto(Book book) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(book.getTitle());
        requestDto.setAuthor(book.getAuthor());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setDescription(book.getDescription());
        requestDto.setCoverImage(book.getCoverImage());
        requestDto.setCategories(Set.of());
        return requestDto;
    }
}
