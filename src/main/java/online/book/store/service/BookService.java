package online.book.store.service;

import java.util.List;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    Page<BookDto> findAll(int page, int size, String sort);

    BookDto getBookById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParametersDto searchParams);

    List<BookDtoWithoutCategoryIds> findAllByCategory(Long id);
}
