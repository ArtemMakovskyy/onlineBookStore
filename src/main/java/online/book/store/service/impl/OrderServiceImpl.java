package online.book.store.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.order.CreateOrderDto;
import online.book.store.dto.order.OrderDto;
import online.book.store.dto.order.UpdateOrderStatusDto;
import online.book.store.dto.order.item.OrderItemDto;
import online.book.store.exception.EmptyDataException;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.OrderItemMapper;
import online.book.store.mapper.OrderMapper;
import online.book.store.model.CartItem;
import online.book.store.model.Order;
import online.book.store.model.OrderItem;
import online.book.store.model.ShoppingCart;
import online.book.store.repository.cart.item.CartItemRepository;
import online.book.store.repository.order.OrderRepository;
import online.book.store.repository.order.item.OrderItemRepository;
import online.book.store.repository.shopping.cart.ShoppingCartRepository;
import online.book.store.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto createUserOrder(Long userId, CreateOrderDto createOrderDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't get ShoppingCart by id " + userId));
        final Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new EmptyDataException("There are no one books in the ShoppingCart. ");
        }
        Order order = new Order();
        Set<OrderItem> orderItems = collectOrderItems(order, cartItems);

        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setTotal(calculatePrice(orderItems));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(createOrderDto.getShippingAddress());
        order.setOrderItems(orderItems);

        cartItemRepository.deleteAll(cartItems);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderDto> findAllOrdersByUser(Long userId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        Page<Order> allByUserId = orderRepository.findAllByUserId(userId, pageable);
        return allByUserId.map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemDto> findAllOrderItemsByOrder(
            Long userId, Long orderId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        return orderItemRepository
                .findAllByOrder_IdAndOrder_User_Id(orderId, userId, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto findOrderItemInOrderByIds(Long orderId, Long itemId) {
        final Order orderFromDb = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id " + orderId));
        OrderItem orderItemFromOrder = orderFromDb.getOrderItems().stream()
                .filter(oi -> oi.getId().equals(itemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find orderItem by id " + itemId));
        return orderItemMapper.toDto(orderItemFromOrder);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto updateStatus) {
        final Order orderFromDb = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id " + orderId));
        orderFromDb.setStatus(updateStatus.getStatus());
        return orderMapper.toDto(orderRepository.save(orderFromDb));
    }

    private Set<OrderItem> collectOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal calculatePrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(oi -> BigDecimal.valueOf(oi.getQuantity()).multiply(oi.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Sort.Order parseSortOrder(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        String direction = parts[1].toUpperCase();
        return new Sort.Order(Sort.Direction.valueOf(direction), property);
    }
}
