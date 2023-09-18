package online.book.store.service;

import online.book.store.dto.order.CreateOrderDto;
import online.book.store.dto.order.OrderDto;
import online.book.store.dto.order.UpdateOrderStatusDto;
import online.book.store.dto.order.item.OrderItemDto;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderDto createUserOrder(Long userId, CreateOrderDto createOrderDto);

    Page<OrderDto> findAllOrdersByUser(Long userId, int page, int size, String sort);

    Page<OrderItemDto> findAllOrderItemsByOrder(
            Long userId, Long orderId, int page, int size, String sort);

    OrderItemDto findOrderItemInOrderByIds(Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto updateStatus);
}
