package application.models.ports.in;

import application.data.ports.out.ICartItemRepository;
import application.data.ports.out.ICartRepository;
import application.data.entities.Cart;
import application.data.entities.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ICartUseCase {

    Cart activeCartFor(Long buyerId);

    Cart addItem(Long buyerId, Long productId, Long variantId, int quantity);

    Cart updateItemQuantity(Long buyerId, Long itemId, int quantity);

    Cart removeItem(Long buyerId, Long itemId);

    Cart clear(Long buyerId);

    List<CartItem> items(Long buyerId);

    Cart requireNotEmpty(Long buyerId);

    Cart close(Long cartId);
}
