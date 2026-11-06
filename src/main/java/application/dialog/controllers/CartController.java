package application.dialog.controllers;

import application.data.entities.Cart;
import application.data.entities.CartItem;
import application.dialog.requests.CartItemRequest;
import application.models.ports.in.ICartUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "07 Carrito", description = "Seleccion provisional de productos antes de comprometerse")
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final ICartUseCase cartUseCase;

    public CartController(ICartUseCase cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @GetMapping("/{buyerId}")
    public Cart activeCart(@PathVariable Long buyerId) {
        return cartUseCase.activeCartFor(buyerId);
    }

    @GetMapping("/{buyerId}/items")
    public List<CartItem> items(@PathVariable Long buyerId) {
        return cartUseCase.items(buyerId);
    }

    @PostMapping("/{buyerId}/items")
    public Cart addItem(@PathVariable Long buyerId, @RequestBody CartItemRequest request) {
        return cartUseCase.addItem(buyerId, request.productId(), request.variantId(), request.quantity());
    }

    @PutMapping("/{buyerId}/items/{itemId}")
    public Cart updateItemQuantity(@PathVariable Long buyerId,
                                   @PathVariable Long itemId,
                                   @RequestParam int quantity) {
        return cartUseCase.updateItemQuantity(buyerId, itemId, quantity);
    }

    @DeleteMapping("/{buyerId}/items/{itemId}")
    public Cart removeItem(@PathVariable Long buyerId, @PathVariable Long itemId) {
        return cartUseCase.removeItem(buyerId, itemId);
    }

    @DeleteMapping("/{buyerId}/items")
    public Cart clear(@PathVariable Long buyerId) {
        return cartUseCase.clear(buyerId);
    }
}
