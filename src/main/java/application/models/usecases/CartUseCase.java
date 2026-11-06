package application.models.usecases;

import application.models.ports.in.ICartUseCase;
import application.models.services.CartService;
import application.data.ports.out.ICartItemRepository;
import application.data.ports.out.ICartRepository;
import application.data.entities.Cart;
import application.data.entities.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CartUseCase implements ICartUseCase {

    private final CartService cartService;

    public CartUseCase(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public Cart activeCartFor(Long buyerId) {
        return cartService.activeCartFor(buyerId);
    }

    @Override
    public Cart addItem(Long buyerId, Long productId, Long variantId, int quantity) {
        return cartService.addItem(buyerId, productId, variantId, quantity);
    }

    @Override
    public Cart updateItemQuantity(Long buyerId, Long itemId, int quantity) {
        return cartService.updateItemQuantity(buyerId, itemId, quantity);
    }

    @Override
    public Cart removeItem(Long buyerId, Long itemId) {
        return cartService.removeItem(buyerId, itemId);
    }

    @Override
    public Cart clear(Long buyerId) {
        return cartService.clear(buyerId);
    }

    @Override
    public List<CartItem> items(Long buyerId) {
        return cartService.items(buyerId);
    }

    @Override
    public Cart requireNotEmpty(Long buyerId) {
        return cartService.requireNotEmpty(buyerId);
    }

    @Override
    public Cart close(Long cartId) {
        return cartService.close(cartId);
    }
}
