package application.dialog.controllers;

import application.data.entities.ProductReturn;
import application.data.entities.ProductReturnDetail;
import application.dialog.requests.ReturnRequest;
import application.dialog.requests.WarehouseOperationRequest;
import application.models.services.ReturnService;

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

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "11 Devoluciones", description = "Solicitud, revision y recepcion de mercancia devuelta")
@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductReturn request(@RequestBody ReturnRequest request) {
        return returnService.request(
                request.buyerId(),
                request.orderId(),
                request.reason(),
                request.toDetails());
    }

    @GetMapping
    public List<ProductReturn> find(@RequestParam(required = false) Long orderId,
                                    @RequestParam(required = false) Long buyerId) {
        if (orderId != null) {
            return returnService.findByOrder(orderId);
        }
        return buyerId == null ? returnService.findAll() : returnService.findByBuyer(buyerId);
    }

    @GetMapping("/{id}")
    public ProductReturn findById(@PathVariable Long id) {
        return returnService.findByIdWithDetails(id);
    }

    @GetMapping("/by-number/{returnNumber}")
    public ProductReturn findByNumber(@PathVariable String returnNumber) {
        return returnService.findByNumber(returnNumber);
    }

    @GetMapping("/{id}/details")
    public List<ProductReturnDetail> details(@PathVariable Long id) {
        return returnService.details(id);
    }

    @GetMapping("/{id}/returned-value")
    public BigDecimal returnedValue(@PathVariable Long id) {
        return returnService.returnedValue(id);
    }

    @PutMapping("/{id}/approval")
    public ProductReturn approve(@PathVariable Long id, @RequestParam Long reviewerId) {
        return returnService.approve(id, reviewerId);
    }

    @PutMapping("/{id}/rejection")
    public ProductReturn reject(@PathVariable Long id, @RequestParam Long reviewerId) {
        return returnService.reject(id, reviewerId);
    }

    @PutMapping("/{id}/reception")
    public ProductReturn receive(@PathVariable Long id, @RequestBody WarehouseOperationRequest request) {
        return returnService.receive(id, request.warehouseId(), request.userId());
    }

    @PutMapping("/{id}/completion")
    public ProductReturn complete(@PathVariable Long id) {
        return returnService.complete(id);
    }
}
