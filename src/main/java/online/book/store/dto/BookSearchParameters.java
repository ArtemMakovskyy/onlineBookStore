package online.book.store.dto;

public record BookSearchParameters(String[] title,
                                   String[] author,
                                   String[] isbn,
                                   String[] price) {
}
