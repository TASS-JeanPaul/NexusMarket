package application.dialog.controllers;

import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.enums.OrderStatus;
import application.dialog.requests.CheckoutRequest;
import application.dialog.requests.WarehouseOperationRequest;
import application.models.services.OrderService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "08 Pedidos", description = "El compromiso comercial formal y su ciclo de vida")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(@RequestBody CheckoutRequest request) {
        return orderService.checkout(request.buyerId(), request.deliveryAddressId(), request.warehouseId());
    }

    @GetMapping
    public List<Order> find(@RequestParam(required = false) Long buyerId,
                            @RequestParam(required = false) OrderStatus status) {
        if (buyerId != null) {
            return orderService.findByBuyer(buyerId);
        }
        return status == null ? orderService.findAll() : orderService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return orderService.findByIdWithDetails(id);
    }

    @GetMapping("/by-number/{orderNumber}")
    public Order findByNumber(@PathVariable String orderNumber) {
        return orderService.findByNumber(orderNumber);
    }

    @GetMapping("/{id}/details")
    public List<OrderDetail> details(@PathVariable Long id) {
        return orderService.details(id);
    }

    @PostMapping("/{id}/payment")
    public Order markAsPaid(@PathVariable Long id, @RequestBody WarehouseOperationRequest request) {
        return orderService.markAsPaid(id, request.warehouseId(), request.userId());
    }

    @PostMapping("/{id}/cancellation")
    public Order cancel(@PathVariable Long id, @RequestBody WarehouseOperationRequest request) {
        return orderService.cancel(id, request.warehouseId(), request.userId());
    }

    @PostMapping("/{id}/completion")
    public Order complete(@PathVariable Long id) {
        return orderService.complete(id);
    }
}
