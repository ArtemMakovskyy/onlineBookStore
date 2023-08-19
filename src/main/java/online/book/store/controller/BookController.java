package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.BookDto;
import online.book.store.dto.BookSearchParametersDto;
import online.book.store.dto.CreateBookRequestDto;
import online.book.store.mapper.BookMapper;
import online.book.store.repository.book.BookRepository;
import online.book.store.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

@Tag(name = "Book management", description = "Endpoints to managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creating a new book.",
            description = "Creating a new book with valid data. "
                    + "Title, author, isbn should be not blank and in addition, "
                    + "isbn must be unique.")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @GetMapping
    @Operation(summary = "Getting page available books.",
            description = "Retrieve page with available books. "
                    + "By default it is first page with 5 books, sorted ASC"
                    + "except those deleted using soft delete.")
    public Page<BookDto> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "price,ASC") String sort) {
        return bookService.findAll(page, size, sort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting available book by id.",
            description = "Retrieve available book by id, "
                    + "if it has not been deleted with soft delete.")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleting book by id",
            description = "Soft deleting available book by id")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updating book by id",
            description = "Updating available book by id, "
                    + "except those deleted using soft delete.")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Book search by parameters.",
            description = "Search available books using criteria by parameters : title, "
                    + "author,isbn, price, except those deleted using soft delete.")
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters) {
        return bookService.search(searchParameters);
    }
}
