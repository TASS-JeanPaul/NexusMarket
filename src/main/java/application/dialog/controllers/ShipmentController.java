package application.dialog.controllers;

import application.data.entities.Shipment;
import application.data.enums.ShipmentStatus;
import application.dialog.requests.ShipmentRequest;
import application.models.ports.in.IShipmentUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

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

    private final IShipmentUseCase shipmentUseCase;

    public ShipmentController(IShipmentUseCase shipmentUseCase) {
        this.shipmentUseCase = shipmentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createFor(@Valid @RequestBody ShipmentRequest request) {
        return shipmentUseCase.createFor(
                request.orderId(),
                request.sourceWarehouseId(),
                request.logisticsOperatorId(),
                request.carrier(),
                request.deliveryAddress());
    }

    @GetMapping
    public List<Shipment> find(@RequestParam(required = false) ShipmentStatus status) {
        return status == null ? shipmentUseCase.findAll() : shipmentUseCase.findByStatus(status);
    }

    @GetMapping("/{id}")
    public Shipment findById(@PathVariable Long id) {
        return shipmentUseCase.findById(id);
    }

    @GetMapping("/by-order")
    public Shipment findByOrder(@RequestParam Long orderId) {
        return shipmentUseCase.findByOrder(orderId);
    }

    @GetMapping("/by-tracking/{trackingNumber}")
    public Shipment findByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentUseCase.findByTrackingNumber(trackingNumber);
    }

    @PutMapping("/{id}/preparation")
    public Shipment markInPreparation(@PathVariable Long id) {
        return shipmentUseCase.markInPreparation(id);
    }

    @PutMapping("/{id}/dispatch")
    public Shipment markAsShipped(@PathVariable Long id) {
        return shipmentUseCase.markAsShipped(id);
    }

    @PutMapping("/{id}/transit")
    public Shipment markInTransit(@PathVariable Long id) {
        return shipmentUseCase.markInTransit(id);
    }

    @PutMapping("/{id}/delivery")
    public Shipment markAsDelivered(@PathVariable Long id) {
        return shipmentUseCase.markAsDelivered(id);
    }

    @PutMapping("/{id}/failure")
    public Shipment markAsFailed(@PathVariable Long id) {
        return shipmentUseCase.markAsFailed(id);
    }

    @PutMapping("/{id}/operator")
    public Shipment reassignOperator(@PathVariable Long id, @RequestParam Long logisticsOperatorId) {
        return shipmentUseCase.reassignOperator(id, logisticsOperatorId);
    }
}
