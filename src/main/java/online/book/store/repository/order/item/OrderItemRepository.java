package online.book.store.repository.order.item;

import online.book.store.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAllByOrder_IdAndOrder_User_Id(Long orderId, Long userId, Pageable pageable);
}
