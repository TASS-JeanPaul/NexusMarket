package application.dialog.controllers;

import application.data.entities.Seller;
import application.dialog.requests.BusinessInformationRequest;
import application.models.services.SellerService;

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

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Seller register(@RequestBody Seller seller, @RequestParam Long administratorId) {
        return sellerService.register(seller, administratorId);
    }

    @GetMapping
    public List<Seller> findAll() {
        return sellerService.findAll();
    }

    @GetMapping("/{id}")
    public Seller findById(@PathVariable Long id) {
        return sellerService.findById(id);
    }

    @GetMapping("/by-tax-id/{taxId}")
    public Seller findByTaxId(@PathVariable String taxId) {
        return sellerService.findByTaxId(taxId);
    }

    @PutMapping("/{id}/business-information")
    public Seller updateBusinessInformation(@PathVariable Long id,
                                            @RequestBody BusinessInformationRequest request) {
        return sellerService.updateBusinessInformation(id, request.businessName(), request.phone());
    }

    @PutMapping("/{id}/block")
    public Seller block(@PathVariable Long id) {
        return sellerService.block(id);
    }

    @PutMapping("/{id}/activation")
    public Seller activate(@PathVariable Long id) {
        return sellerService.activate(id);
    }
}
