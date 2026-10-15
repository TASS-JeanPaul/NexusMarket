package application.models.services;

import application.data.enums.OrderStatus;
import application.data.enums.Role;
import application.data.enums.ShipmentStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IShipmentRepository;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;
import application.data.entities.Shipment;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ShipmentService extends BaseService {

    private static final Map<ShipmentStatus, Set<ShipmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            ShipmentStatus.PENDING, EnumSet.of(ShipmentStatus.IN_PREPARATION, ShipmentStatus.FAILED),
            ShipmentStatus.IN_PREPARATION, EnumSet.of(ShipmentStatus.SHIPPED, ShipmentStatus.FAILED),
            ShipmentStatus.SHIPPED, EnumSet.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.FAILED),
            ShipmentStatus.IN_TRANSIT, EnumSet.of(ShipmentStatus.DELIVERED, ShipmentStatus.FAILED),
            ShipmentStatus.DELIVERED, EnumSet.noneOf(ShipmentStatus.class),
            ShipmentStatus.FAILED, EnumSet.noneOf(ShipmentStatus.class));

    private final IShipmentRepository shipmentRepository;
    private final CatalogService catalogService;
    private final WarehouseService warehouseService;
    private final UserService userService;
    private final OrderService orderService;

    public ShipmentService(IShipmentRepository shipmentRepository,
                           CatalogService catalogService,
                           WarehouseService warehouseService,
                           UserService userService,
                           OrderService orderService) {
        this.shipmentRepository = shipmentRepository;
        this.catalogService = catalogService;
        this.warehouseService = warehouseService;
        this.userService = userService;
        this.orderService = orderService;
    }

    public Shipment createFor(Long orderId, Long sourceWarehouseId, Long logisticsOperatorId,
                              String carrier, String deliveryAddress) {
        return execute(() -> {
            Order order = orderService.findByIdWithDetails(orderId);
            require(order.getStatus() == OrderStatus.PAID,
                    "Only a paid order can be dispatched, and the order " + orderId + " is " + order.getStatus());
            require(containsShippableProducts(order.getDetails()),
                    "The order " + orderId + " only contains products delivered without dispatch");
            require(shipmentRepository.findByOrderId(orderId).isEmpty(),
                    "The order " + orderId + " already has a shipment registered");

            warehouseService.requireActive(sourceWarehouseId);
            userService.requireRole(logisticsOperatorId, Role.LOGISTICS_OPERATOR);
            requireText(carrier, "The carrier");
            requireText(deliveryAddress, "The delivery address");

            Shipment shipment = new Shipment();
            shipment.setOrderId(orderId);
            shipment.setSourceWarehouseId(sourceWarehouseId);
            shipment.setLogisticsOperatorId(logisticsOperatorId);
            shipment.setTrackingNumber(generateDocumentNumber("SHP"));
            shipment.setCarrier(carrier);
            shipment.setDeliveryAddress(deliveryAddress);
            shipment.setStatus(ShipmentStatus.PENDING);

            return shipmentRepository.save(shipment);
        });
    }

    public Shipment findById(Long id) {
        requireIdentifier(id, "The shipment identifier");
        return execute(() -> findOrFail(shipmentRepository.findById(id), "Shipment", id));
    }

    public List<Shipment> findAll() {
        return execute(shipmentRepository::findAll);
    }

    public List<Shipment> findByStatus(ShipmentStatus status) {
        require(status != null, "The shipment status is required");
        return execute(() -> shipmentRepository.findByStatus(status));
    }

    public Shipment findByOrder(Long orderId) {
        requireIdentifier(orderId, "The order identifier");
        return execute(() -> shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The order " + orderId + " has no shipment registered")));
    }

    public Shipment findByTrackingNumber(String trackingNumber) {
        requireText(trackingNumber, "The tracking number");
        return execute(() -> shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No shipment is registered with the tracking number " + trackingNumber)));
    }

    public Shipment markInPreparation(Long shipmentId) {
        return changeStatus(shipmentId, ShipmentStatus.IN_PREPARATION);
    }

    public Shipment markAsShipped(Long shipmentId) {
        return execute(() -> {
            Shipment shipment = changeStatus(shipmentId, ShipmentStatus.SHIPPED);
            shipment.setDispatchedAt(LocalDateTime.now());
            shipmentRepository.update(shipment);
            orderService.markAsShipped(shipment.getOrderId());
            return shipment;
        });
    }

    public Shipment markInTransit(Long shipmentId) {
        return changeStatus(shipmentId, ShipmentStatus.IN_TRANSIT);
    }

    public Shipment markAsDelivered(Long shipmentId) {
        return execute(() -> {
            Shipment shipment = changeStatus(shipmentId, ShipmentStatus.DELIVERED);
            shipment.setDeliveredAt(LocalDateTime.now());
            shipmentRepository.update(shipment);
            orderService.markAsDelivered(shipment.getOrderId());
            return shipment;
        });
    }

    public Shipment markAsFailed(Long shipmentId) {
        return changeStatus(shipmentId, ShipmentStatus.FAILED);
    }

    public Shipment reassignOperator(Long shipmentId, Long logisticsOperatorId) {
        return execute(() -> {
            Shipment shipment = findById(shipmentId);
            require(shipment.getStatus() != ShipmentStatus.DELIVERED,
                    "A delivered shipment cannot change its logistics operator");
            userService.requireRole(logisticsOperatorId, Role.LOGISTICS_OPERATOR);

            shipment.setLogisticsOperatorId(logisticsOperatorId);
            shipmentRepository.update(shipment);
            return shipment;
        });
    }

    private Shipment changeStatus(Long shipmentId, ShipmentStatus target) {
        return execute(() -> {
            Shipment shipment = findById(shipmentId);
            require(ALLOWED_TRANSITIONS.get(shipment.getStatus()).contains(target),
                    "A shipment cannot move from " + shipment.getStatus() + " to " + target);

            shipment.setStatus(target);
            shipmentRepository.update(shipment);
            return shipment;
        });
    }

    private boolean containsShippableProducts(List<OrderDetail> details) {
        for (OrderDetail detail : details) {
            Product product = catalogService.findProductById(detail.getProductId());
            if (product.requiresShipping()) {
                return true;
            }
        }
        return false;
    }
}
