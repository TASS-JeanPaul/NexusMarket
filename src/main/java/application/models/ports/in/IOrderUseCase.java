package application.models.ports.in;

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

public interface IOrderUseCase {

    Order checkout(Long buyerId, Long deliveryAddressId, Long warehouseId);

    Order findById(Long id);

    Order findByIdWithDetails(Long id);

    List<Order> findAll();

    List<Order> findByBuyer(Long buyerId);

    List<Order> findByStatus(OrderStatus status);

    Order findByNumber(String orderNumber);

    List<OrderDetail> details(Long orderId);

    Order markAsPaid(Long orderId, Long warehouseId, Long userId);

    Order markAsShipped(Long orderId);

    Order markAsDelivered(Long orderId);

    Order complete(Long orderId);

    Order cancel(Long orderId, Long warehouseId, Long userId);

    Order changeStatus(Long orderId, OrderStatus newStatus);

    Order requireReturnable(Long orderId);
}
