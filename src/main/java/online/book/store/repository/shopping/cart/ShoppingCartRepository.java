package online.book.store.repository.shopping.cart;

import java.util.Optional;
import online.book.store.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart sc "
            + "JOIN FETCH sc.cartItems ci "
            + "JOIN FETCH sc.user u "
            + "JOIN FETCH ci.book "
            + "JOIN FETCH u.roles "
            + " where sc.id = :id")
    Optional<ShoppingCart> findById(Long id);
}
