package application.services;

import application.domain.interfaces.ICartItemRepository;
import application.domain.interfaces.ICartRepository;
import application.domain.models.Cart;
import application.domain.models.CartItem;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService extends BaseService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final CatalogService catalogService;
    private final BuyerService buyerService;

    public CartService(ICartRepository cartRepository,
                       ICartItemRepository cartItemRepository,
                       CatalogService catalogService,
                       BuyerService buyerService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.catalogService = catalogService;
        this.buyerService = buyerService;
    }

    public Cart activeCartFor(Long buyerId) {
        return execute(() -> {
            buyerService.requirePurchaseEnabled(buyerId);

            Optional<Cart> existing = cartRepository.findActiveByBuyerId(buyerId);
            if (existing.isPresent()) {
                return loadItems(existing.get());
            }

            Cart cart = new Cart();
            cart.setBuyerId(buyerId);
            cart.setTotal(BigDecimal.ZERO);
            cart.setActive(true);
            cart.setCreatedAt(LocalDateTime.now());

            return loadItems(cartRepository.save(cart));
        });
    }

    public Cart addItem(Long buyerId, Long productId, Long variantId, int quantity) {
        return execute(() -> {
            requirePositive(quantity, "The quantity");
            catalogService.requirePublished(productId);

            Cart cart = activeCartFor(buyerId);
            BigDecimal unitPrice = catalogService.resolvePrice(productId, variantId);

            Optional<CartItem> existing =
                    cartItemRepository.findByCartAndProduct(cart.getId(), productId, variantId);

            if (existing.isPresent()) {
                CartItem item = existing.get();
                item.setQuantity(item.getQuantity() + quantity);
                item.setUnitPrice(unitPrice);
                item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                cartItemRepository.update(item);
            } else {
                CartItem item = new CartItem();
                item.setCartId(cart.getId());
                item.setProductId(productId);
                item.setVariantId(variantId);
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);
                item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
                cartItemRepository.save(item);
            }

            return recalculateTotal(cart);
        });
    }

    public Cart updateItemQuantity(Long buyerId, Long itemId, int quantity) {
        return execute(() -> {
            requirePositive(quantity, "The quantity");

            Cart cart = activeCartFor(buyerId);
            CartItem item = findItemOfCart(cart, itemId);

            item.setQuantity(quantity);
            item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.update(item);

            return recalculateTotal(cart);
        });
    }

    public Cart removeItem(Long buyerId, Long itemId) {
        return execute(() -> {
            Cart cart = activeCartFor(buyerId);
            findItemOfCart(cart, itemId);
            cartItemRepository.delete(itemId);

            return recalculateTotal(cart);
        });
    }

    public Cart clear(Long buyerId) {
        return execute(() -> {
            Cart cart = activeCartFor(buyerId);
            cartItemRepository.deleteByCartId(cart.getId());

            return recalculateTotal(cart);
        });
    }

    public List<CartItem> items(Long buyerId) {
        return activeCartFor(buyerId).getItems();
    }

    public Cart requireNotEmpty(Long buyerId) {
        Cart cart = activeCartFor(buyerId);
        require(!cart.getItems().isEmpty(), "The cart of the buyer " + buyerId + " has no products selected");
        return cart;
    }

    public Cart close(Long cartId) {
        return execute(() -> {
            Cart cart = findOrFail(cartRepository.findById(cartId), "Cart", cartId);
            cart.setActive(false);
            cartRepository.update(cart);
            return cart;
        });
    }

    private CartItem findItemOfCart(Cart cart, Long itemId) {
        requireIdentifier(itemId, "The cart item identifier");
        return execute(() -> {
            CartItem item = findOrFail(cartItemRepository.findById(itemId), "Cart item", itemId);
            require(cart.getId().equals(item.getCartId()),
                    "The item " + itemId + " does not belong to the cart of the buyer " + cart.getBuyerId());
            return item;
        });
    }

    private Cart loadItems(Cart cart) {
        return execute(() -> {
            cart.setItems(cartItemRepository.findByCartId(cart.getId()));
            return cart;
        });
    }

    private Cart recalculateTotal(Cart cart) {
        return execute(() -> {
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            BigDecimal total = BigDecimal.ZERO;

            for (CartItem item : items) {
                total = total.add(item.getSubtotal());
            }

            cart.setItems(items);
            cart.setTotal(total);
            cartRepository.update(cart);
            return cart;
        });
    }
}
