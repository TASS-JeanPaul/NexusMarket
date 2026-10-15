package application.models.services;

import application.data.enums.OrderStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IOrderDetailRepository;
import application.data.contracts.IOrderRepository;
import application.data.entities.Cart;
import application.data.entities.CartItem;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService extends BaseService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.CART, EnumSet.of(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED),
            OrderStatus.PENDING_PAYMENT, EnumSet.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.COMPLETED),
            OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final CatalogService catalogService;
    private final InvoiceService invoiceService;
    private final BuyerService buyerService;

    public OrderService(IOrderRepository orderRepository,
                        IOrderDetailRepository orderDetailRepository,
                        CartService cartService,
                        InventoryService inventoryService,
                        CatalogService catalogService,
                        InvoiceService invoiceService,
                        BuyerService buyerService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartService = cartService;
        this.inventoryService = inventoryService;
        this.catalogService = catalogService;
        this.invoiceService = invoiceService;
        this.buyerService = buyerService;
    }

    public Order checkout(Long buyerId, Long deliveryAddressId, Long warehouseId) {
        return execute(() -> {
            buyerService.requirePurchaseEnabled(buyerId);
            Cart cart = cartService.requireNotEmpty(buyerId);

            List<OrderDetail> details = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            boolean requiresShipping = false;
            boolean managesInventory = false;

            for (CartItem item : cart.getItems()) {
                Product product = catalogService.requirePublished(item.getProductId());
                requiresShipping = requiresShipping || product.requiresShipping();
                managesInventory = managesInventory || product.managesInventory();

                OrderDetail detail = freeze(product, item);
                details.add(detail);
                total = total.add(detail.getSubtotal());
            }

            if (requiresShipping) {
                requireIdentifier(deliveryAddressId,
                        "The delivery address identifier, because the order contains products that are shipped");
                buyerService.requireDeliveryAddress(buyerId, deliveryAddressId);
            }
            if (managesInventory) {
                requireIdentifier(warehouseId,
                        "The dispatch warehouse identifier, because the order contains products held in stock");
            }

            for (OrderDetail detail : details) {
                Product product = catalogService.findProductById(detail.getProductId());
                if (product.managesInventory()) {
                    inventoryService.reserve(detail.getProductId(), detail.getVariantId(), warehouseId,
                            detail.getQuantity(), buyerId);
                }
            }

            Order order = new Order();
            order.setOrderNumber(generateDocumentNumber("ORD"));
            order.setBuyerId(buyerId);
            order.setDeliveryAddressId(requiresShipping ? deliveryAddressId : null);
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            order.setTotal(total);
            order.setCreatedAt(LocalDateTime.now());

            Order stored = orderRepository.save(order);

            for (OrderDetail detail : details) {
                detail.setOrderId(stored.getId());
                orderDetailRepository.save(detail);
            }

            stored.setDetails(details);
            cartService.close(cart.getId());
            return stored;
        });
    }

    public Order findById(Long id) {
        requireIdentifier(id, "The order identifier");
        return execute(() -> findOrFail(orderRepository.findById(id), "Order", id));
    }

    public Order findByIdWithDetails(Long id) {
        return execute(() -> {
            Order order = findById(id);
            order.setDetails(orderDetailRepository.findByOrderId(id));
            return order;
        });
    }

    public List<Order> findAll() {
        return execute(orderRepository::findAll);
    }

    public List<Order> findByBuyer(Long buyerId) {
        requireIdentifier(buyerId, "The buyer identifier");
        return execute(() -> orderRepository.findByBuyerId(buyerId));
    }

    public List<Order> findByStatus(OrderStatus status) {
        require(status != null, "The order status is required");
        return execute(() -> orderRepository.findByStatus(status));
    }

    public Order findByNumber(String orderNumber) {
        requireText(orderNumber, "The order number");
        return execute(() -> orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No order is registered with the number " + orderNumber)));
    }

    public List<OrderDetail> details(Long orderId) {
        findById(orderId);
        return execute(() -> orderDetailRepository.findByOrderId(orderId));
    }

    public Order markAsPaid(Long orderId, Long warehouseId, Long userId) {
        return execute(() -> {
            Order order = findByIdWithDetails(orderId);
            requireTransition(order, OrderStatus.PAID);

            boolean requiresShipping = false;

            for (OrderDetail detail : order.getDetails()) {
                Product product = catalogService.findProductById(detail.getProductId());
                requiresShipping = requiresShipping || product.requiresShipping();

                if (product.managesInventory()) {
                    requireIdentifier(warehouseId,
                            "The warehouse identifier that holds the reservation of the order");
                    inventoryService.confirmSale(detail.getProductId(), detail.getVariantId(), warehouseId,
                            detail.getQuantity(), userId);
                }
            }

            order.setStatus(OrderStatus.PAID);
            orderRepository.update(order);
            invoiceService.issueFor(order);

            if (!requiresShipping) {
                return close(order);
            }
            return order;
        });
    }

    public Order markAsShipped(Long orderId) {
        return changeStatus(orderId, OrderStatus.SHIPPED);
    }

    public Order markAsDelivered(Long orderId) {
        return changeStatus(orderId, OrderStatus.DELIVERED);
    }

    public Order complete(Long orderId) {
        return execute(() -> {
            Order order = findById(orderId);
            requireTransition(order, OrderStatus.COMPLETED);
            return close(order);
        });
    }

    public Order cancel(Long orderId, Long warehouseId, Long userId) {
        return execute(() -> {
            Order order = findByIdWithDetails(orderId);
            requireTransition(order, OrderStatus.CANCELLED);

            for (OrderDetail detail : order.getDetails()) {
                Product product = catalogService.findProductById(detail.getProductId());
                if (product.managesInventory()) {
                    requireIdentifier(warehouseId,
                            "The warehouse identifier that holds the reservation of the order");
                    inventoryService.release(detail.getProductId(), detail.getVariantId(), warehouseId,
                            detail.getQuantity(), userId);
                }
            }

            order.setStatus(OrderStatus.CANCELLED);
            order.setCompletedAt(LocalDateTime.now());
            orderRepository.update(order);
            return order;
        });
    }

    public Order changeStatus(Long orderId, OrderStatus newStatus) {
        return execute(() -> {
            Order order = findById(orderId);
            requireTransition(order, newStatus);

            order.setStatus(newStatus);
            orderRepository.update(order);
            return order;
        });
    }

    public Order requireReturnable(Long orderId) {
        Order order = findById(orderId);
        require(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED,
                "Only a delivered order admits a return, and the order " + orderId + " is " + order.getStatus());
        return order;
    }

    private Order close(Order order) {
        return execute(() -> {
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
            orderRepository.update(order);
            return order;
        });
    }

    private OrderDetail freeze(Product product, CartItem item) {
        requirePositive(item.getQuantity(), "The quantity of the item " + item.getId());

        BigDecimal unitPrice = catalogService.resolvePrice(item.getProductId(), item.getVariantId());

        OrderDetail detail = new OrderDetail();
        detail.setProductId(product.getId());
        detail.setVariantId(item.getVariantId());
        detail.setProductName(product.getName());
        detail.setQuantity(item.getQuantity());
        detail.setUnitPrice(unitPrice);
        detail.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        return detail;
    }

    private void requireTransition(Order order, OrderStatus target) {
        require(target != null, "The target status is required");
        require(order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED,
                "The order " + order.getId() + " is finalized and cannot be modified under any circumstance");
        require(ALLOWED_TRANSITIONS.get(order.getStatus()).contains(target),
                "An order cannot move from " + order.getStatus() + " to " + target);
    }
}
