package application.models.usecases;

import application.models.ports.in.IShipmentUseCase;
import application.models.services.ShipmentService;
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

import org.springframework.stereotype.Service;

@Service
public class ShipmentUseCase implements IShipmentUseCase {

    private final ShipmentService shipmentService;

    public ShipmentUseCase(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @Override
    public Shipment createFor(Long orderId, Long sourceWarehouseId, Long logisticsOperatorId, String carrier, String deliveryAddress) {
        return shipmentService.createFor(orderId, sourceWarehouseId, logisticsOperatorId, carrier, deliveryAddress);
    }

    @Override
    public Shipment findById(Long id) {
        return shipmentService.findById(id);
    }

    @Override
    public List<Shipment> findAll() {
        return shipmentService.findAll();
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentService.findByStatus(status);
    }

    @Override
    public Shipment findByOrder(Long orderId) {
        return shipmentService.findByOrder(orderId);
    }

    @Override
    public Shipment findByTrackingNumber(String trackingNumber) {
        return shipmentService.findByTrackingNumber(trackingNumber);
    }

    @Override
    public Shipment markInPreparation(Long shipmentId) {
        return shipmentService.markInPreparation(shipmentId);
    }

    @Override
    public Shipment markAsShipped(Long shipmentId) {
        return shipmentService.markAsShipped(shipmentId);
    }

    @Override
    public Shipment markInTransit(Long shipmentId) {
        return shipmentService.markInTransit(shipmentId);
    }

    @Override
    public Shipment markAsDelivered(Long shipmentId) {
        return shipmentService.markAsDelivered(shipmentId);
    }

    @Override
    public Shipment markAsFailed(Long shipmentId) {
        return shipmentService.markAsFailed(shipmentId);
    }

    @Override
    public Shipment reassignOperator(Long shipmentId, Long logisticsOperatorId) {
        return shipmentService.reassignOperator(shipmentId, logisticsOperatorId);
    }
}
