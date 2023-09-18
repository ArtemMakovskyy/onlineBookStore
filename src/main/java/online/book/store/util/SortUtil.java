package online.book.store.util;

import org.springframework.data.domain.Sort;

public final class SortUtil {
    private SortUtil() {
    }

    public static Sort.Order parseSortOrder(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        String direction = parts[1].toUpperCase();
        return new Sort.Order(Sort.Direction.valueOf(direction), property);
    }
}
