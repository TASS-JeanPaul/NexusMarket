package application.dialog.controllers;

import application.data.entities.ProductReturn;
import application.data.entities.ProductReturnDetail;
import application.dialog.requests.ReturnRequest;
import application.dialog.requests.WarehouseOperationRequest;
import application.models.ports.in.IReturnUseCase;

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

    private final IReturnUseCase returnUseCase;

    public ReturnController(IReturnUseCase returnUseCase) {
        this.returnUseCase = returnUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductReturn request(@RequestBody ReturnRequest request) {
        return returnUseCase.request(
                request.buyerId(),
                request.orderId(),
                request.reason(),
                request.toDetails());
    }

    @GetMapping
    public List<ProductReturn> find(@RequestParam(required = false) Long orderId,
                                    @RequestParam(required = false) Long buyerId) {
        if (orderId != null) {
            return returnUseCase.findByOrder(orderId);
        }
        return buyerId == null ? returnUseCase.findAll() : returnUseCase.findByBuyer(buyerId);
    }

    @GetMapping("/{id}")
    public ProductReturn findById(@PathVariable Long id) {
        return returnUseCase.findByIdWithDetails(id);
    }

    @GetMapping("/by-number/{returnNumber}")
    public ProductReturn findByNumber(@PathVariable String returnNumber) {
        return returnUseCase.findByNumber(returnNumber);
    }

    @GetMapping("/{id}/details")
    public List<ProductReturnDetail> details(@PathVariable Long id) {
        return returnUseCase.details(id);
    }

    @GetMapping("/{id}/returned-value")
    public BigDecimal returnedValue(@PathVariable Long id) {
        return returnUseCase.returnedValue(id);
    }

    @PutMapping("/{id}/approval")
    public ProductReturn approve(@PathVariable Long id, @RequestParam Long reviewerId) {
        return returnUseCase.approve(id, reviewerId);
    }

    @PutMapping("/{id}/rejection")
    public ProductReturn reject(@PathVariable Long id, @RequestParam Long reviewerId) {
        return returnUseCase.reject(id, reviewerId);
    }

    @PutMapping("/{id}/reception")
    public ProductReturn receive(@PathVariable Long id, @RequestBody WarehouseOperationRequest request) {
        return returnUseCase.receive(id, request.warehouseId(), request.userId());
    }

    @PutMapping("/{id}/completion")
    public ProductReturn complete(@PathVariable Long id) {
        return returnUseCase.complete(id);
    }
}
