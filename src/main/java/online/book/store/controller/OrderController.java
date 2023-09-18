package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.order.CreateOrderDto;
import online.book.store.dto.order.OrderDto;
import online.book.store.dto.order.UpdateOrderStatusDto;
import online.book.store.dto.order.item.OrderItemDto;
import online.book.store.model.User;
import online.book.store.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Management to place an order.",
        description = "Endpoints to place an order")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Place an order.",
            description = "Place an order.")
    public OrderDto createUsersOrder(
            Authentication authentication,
            @RequestBody CreateOrderDto createOrderDto) {
        final User user = (User) authentication.getPrincipal();
        return orderService.createUserOrder(user.getId(), createOrderDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "View order history of user.",
            description = "View order history of user.")
    public Page<OrderDto> findAllOrdersByUser(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,ASC") String sort) {
        final User user = (User) authentication.getPrincipal();
        return orderService.findAllOrdersByUser(user.getId(), page, size, sort);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve all OrderItems for a specific order.",
            description = "Retrieve all OrderItems for a specific order.")
    public Page<OrderItemDto> findAllOrderItemsByOrder(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,ASC") String sort,
            @PathVariable Long orderId) {
        final User user = (User) authentication.getPrincipal();
        return orderService.findAllOrderItemsByOrder(user.getId(), orderId, page, size, sort);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve a specific OrderItem within an order.",
            description = "Retrieve a specific OrderItem within an order.")
    public OrderItemDto findOrderItemsFromOrderById(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return orderService.findOrderItemInOrderByIds(orderId, itemId);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update the status of an order.",
            description = "Update the status of an order.")
    public OrderDto updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusDto updateStatus) {
        return orderService.updateOrderStatus(orderId, updateStatus);
    }
}
