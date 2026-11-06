package application.dialog.controllers;

import application.data.entities.Seller;
import application.dialog.requests.BusinessInformationRequest;
import application.models.ports.in.ISellerUseCase;

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

@Tag(name = "03 Vendedores", description = "Vendedores, incorporados siempre por un administrador")
@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final ISellerUseCase sellerUseCase;

    public SellerController(ISellerUseCase sellerUseCase) {
        this.sellerUseCase = sellerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Seller register(@Valid @RequestBody Seller seller, @RequestParam Long administratorId) {
        return sellerUseCase.register(seller, administratorId);
    }

    @GetMapping
    public List<Seller> findAll() {
        return sellerUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Seller findById(@PathVariable Long id) {
        return sellerUseCase.findById(id);
    }

    @GetMapping("/by-tax-id/{taxId}")
    public Seller findByTaxId(@PathVariable String taxId) {
        return sellerUseCase.findByTaxId(taxId);
    }

    @PutMapping("/{id}/business-information")
    public Seller updateBusinessInformation(@PathVariable Long id,
                                            @Valid @RequestBody BusinessInformationRequest request) {
        return sellerUseCase.updateBusinessInformation(id, request.businessName(), request.phone());
    }

    @PutMapping("/{id}/block")
    public Seller block(@PathVariable Long id) {
        return sellerUseCase.block(id);
    }

    @PutMapping("/{id}/activation")
    public Seller activate(@PathVariable Long id) {
        return sellerUseCase.activate(id);
    }
}
