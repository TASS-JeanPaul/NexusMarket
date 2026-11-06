package application.models.usecases;

import application.models.ports.in.IOrderUseCase;
import application.models.services.OrderService;
import application.data.enums.OrderStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IOrderDetailRepository;
import application.data.ports.out.IOrderRepository;
import application.data.entities.Cart;
import application.data.entities.CartItem;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class OrderUseCase implements IOrderUseCase {

    private final OrderService orderService;

    public OrderUseCase(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Order checkout(Long buyerId, Long deliveryAddressId, Long warehouseId) {
        return orderService.checkout(buyerId, deliveryAddressId, warehouseId);
    }

    @Override
    public Order findById(Long id) {
        return orderService.findById(id);
    }

    @Override
    public Order findByIdWithDetails(Long id) {
        return orderService.findByIdWithDetails(id);
    }

    @Override
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @Override
    public List<Order> findByBuyer(Long buyerId) {
        return orderService.findByBuyer(buyerId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderService.findByStatus(status);
    }

    @Override
    public Order findByNumber(String orderNumber) {
        return orderService.findByNumber(orderNumber);
    }

    @Override
    public List<OrderDetail> details(Long orderId) {
        return orderService.details(orderId);
    }

    @Override
    public Order markAsPaid(Long orderId, Long warehouseId, Long userId) {
        return orderService.markAsPaid(orderId, warehouseId, userId);
    }

    @Override
    public Order markAsShipped(Long orderId) {
        return orderService.markAsShipped(orderId);
    }

    @Override
    public Order markAsDelivered(Long orderId) {
        return orderService.markAsDelivered(orderId);
    }

    @Override
    public Order complete(Long orderId) {
        return orderService.complete(orderId);
    }

    @Override
    public Order cancel(Long orderId, Long warehouseId, Long userId) {
        return orderService.cancel(orderId, warehouseId, userId);
    }

    @Override
    public Order changeStatus(Long orderId, OrderStatus newStatus) {
        return orderService.changeStatus(orderId, newStatus);
    }

    @Override
    public Order requireReturnable(Long orderId) {
        return orderService.requireReturnable(orderId);
    }
}
