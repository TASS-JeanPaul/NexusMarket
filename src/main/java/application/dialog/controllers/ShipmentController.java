package application.dialog.controllers;

import application.data.entities.Shipment;
import application.data.enums.ShipmentStatus;
import application.dialog.requests.ShipmentRequest;
import application.models.services.ShipmentService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "10 Envios", description = "Traslado fisico de los pedidos que requieren despacho")
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createFor(@RequestBody ShipmentRequest request) {
        return shipmentService.createFor(
                request.orderId(),
                request.sourceWarehouseId(),
                request.logisticsOperatorId(),
                request.carrier(),
                request.deliveryAddress());
    }

    @GetMapping
    public List<Shipment> find(@RequestParam(required = false) ShipmentStatus status) {
        return status == null ? shipmentService.findAll() : shipmentService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public Shipment findById(@PathVariable Long id) {
        return shipmentService.findById(id);
    }

    @GetMapping("/by-order")
    public Shipment findByOrder(@RequestParam Long orderId) {
        return shipmentService.findByOrder(orderId);
    }

    @GetMapping("/by-tracking/{trackingNumber}")
    public Shipment findByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.findByTrackingNumber(trackingNumber);
    }

    @PutMapping("/{id}/preparation")
    public Shipment markInPreparation(@PathVariable Long id) {
        return shipmentService.markInPreparation(id);
    }

    @PutMapping("/{id}/dispatch")
    public Shipment markAsShipped(@PathVariable Long id) {
        return shipmentService.markAsShipped(id);
    }

    @PutMapping("/{id}/transit")
    public Shipment markInTransit(@PathVariable Long id) {
        return shipmentService.markInTransit(id);
    }

    @PutMapping("/{id}/delivery")
    public Shipment markAsDelivered(@PathVariable Long id) {
        return shipmentService.markAsDelivered(id);
    }

    @PutMapping("/{id}/failure")
    public Shipment markAsFailed(@PathVariable Long id) {
        return shipmentService.markAsFailed(id);
    }

    @PutMapping("/{id}/operator")
    public Shipment reassignOperator(@PathVariable Long id, @RequestParam Long logisticsOperatorId) {
        return shipmentService.reassignOperator(id, logisticsOperatorId);
    }
}
