package book_store.service.impl;

import book_store.dto.BookDto;
import book_store.dto.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.model.Book;
import book_store.repository.book.BookRepository;
import book_store.repository.book.BookSpecificationBuilderImpl;
import book_store.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilderImpl bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book savedBook = bookRepository.save(bookMapper.toModel(requestDto));
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.getBookById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't get book by id " + id)));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        if (bookRepository.existsById(id)) {
            Book book = bookMapper.toModel(requestDto);
            book.setId(id);
            Book savedBook = bookRepository.save(book);
            return bookMapper.toDto(savedBook);
        }
        throw new EntityNotFoundException("Book by id " + id + " does not exist");
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParams) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParams);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
