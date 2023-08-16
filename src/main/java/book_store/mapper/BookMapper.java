package book_store.mapper;


import book_store.config.MapperConfig;
import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto response);
}
