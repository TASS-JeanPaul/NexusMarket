package application.models.ports.in;

import application.data.enums.OrderStatus;
import application.data.enums.Role;
import application.data.enums.ShipmentStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IShipmentRepository;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;
import application.data.entities.Shipment;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IShipmentUseCase {

    Shipment createFor(Long orderId, Long sourceWarehouseId, Long logisticsOperatorId, String carrier, String deliveryAddress);

    Shipment findById(Long id);

    List<Shipment> findAll();

    List<Shipment> findByStatus(ShipmentStatus status);

    Shipment findByOrder(Long orderId);

    Shipment findByTrackingNumber(String trackingNumber);

    Shipment markInPreparation(Long shipmentId);

    Shipment markAsShipped(Long shipmentId);

    Shipment markInTransit(Long shipmentId);

    Shipment markAsDelivered(Long shipmentId);

    Shipment markAsFailed(Long shipmentId);

    Shipment reassignOperator(Long shipmentId, Long logisticsOperatorId);
}
