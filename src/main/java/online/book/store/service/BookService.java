package online.book.store.service;

import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    Page<BookDto> findAll(int page, int size, String sort);

    BookDto getBookById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    Page<BookDto> search(BookSearchParametersDto searchParams, Pageable pageable);

    Page<BookDtoWithoutCategoryIds> findAllByCategory(Long id, int page, int size, String sort);
}
