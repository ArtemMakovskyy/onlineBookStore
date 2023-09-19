package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.cart.item.AddBookToTheShoppingCartDto;
import online.book.store.dto.cart.item.BookQuantityDto;
import online.book.store.dto.shopping.cart.ShoppingCartDto;
import online.book.store.model.User;
import online.book.store.service.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart management", description = "Endpoints to managing Shopping Carts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve user's shopping cart.",
            description = "Retrieve user's shopping cart with books and quantities")
    public ShoppingCartDto getShoppingCartById(Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartDtoByUserId(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add book to the shopping cart.",
            description = "Add book with quantity to the shopping cart")
    public AddBookToTheShoppingCartDto addBookToTheShoppingCart(
            Authentication authentication,
            @RequestBody @Valid AddBookToTheShoppingCartDto addBookDto) {
        final User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBook(addBookDto, user.getId());
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update quantity of a book in the shopping cart.",
            description = "Update quantity of a book in the shopping cart by CartItem id")
    public BookQuantityDto updateBookQuantity(
            @PathVariable Long cartItemId,
            @RequestBody @Valid BookQuantityDto updateBookQuantityDto) {
        return shoppingCartService.updateBookQuantity(updateBookQuantityDto, cartItemId);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Remove a book from the shopping cart.",
            description = "Soft delete book from the shopping cart")
    public void delete(@PathVariable Long cartItemId) {
        shoppingCartService.deleteCartItem(cartItemId);
    }
}
