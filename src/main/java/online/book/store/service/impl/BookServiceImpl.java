package online.book.store.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.BookMapper;
import online.book.store.model.Book;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.book.BookSpecificationBuilderImpl;
import online.book.store.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilderImpl bookSpecificationBuilder;

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.getBookById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get book by id " + id)));
    }

    @Override
    public Page<BookDto> findAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        final List<BookDto> bookDtoList = bookRepository.findAll(pageable)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(bookDtoList, pageable, bookRepository.count());
    }

    private Sort.Order parseSortOrder(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        String direction = parts[1].toUpperCase();
        return new Sort.Order(Sort.Direction.valueOf(direction), property);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        if (bookRepository.existsById(id)) {
            Book book = bookMapper.toEntity(requestDto);
            book.setId(id);
            Book savedBook = bookRepository.save(book);
            return bookMapper.toDto(savedBook);
        }
        throw new EntityNotFoundException("Book by id " + id + " does not exist");
    }

    @Override
    public Page<BookDto> search(BookSearchParametersDto searchParams, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParams);
        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategory(
            Long id, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        final List<BookDtoWithoutCategoryIds> bookDtoWithoutCategoryIds
                = bookRepository.findAllByCategoriesId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories).toList();
        return new PageImpl<>(bookDtoWithoutCategoryIds, pageable, bookRepository.count());
    }

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        System.out.println(requestDto);
        Book savedBook = bookRepository.save(bookMapper.toEntity(requestDto));
        return bookMapper.toDto(savedBook);
    }
}
