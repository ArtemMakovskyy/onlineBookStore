package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.order.item.OrderItemDto;
import online.book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);
}
