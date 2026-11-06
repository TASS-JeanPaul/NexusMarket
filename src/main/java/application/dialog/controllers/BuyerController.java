package application.dialog.controllers;

import application.data.entities.Address;
import application.data.entities.Buyer;
import application.models.ports.in.IBuyerUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "02 Compradores", description = "Compradores, su estado comercial y sus direcciones de entrega")
@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    private final IBuyerUseCase buyerUseCase;

    public BuyerController(IBuyerUseCase buyerUseCase) {
        this.buyerUseCase = buyerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Buyer register(@Valid @RequestBody Buyer buyer) {
        return buyerUseCase.register(buyer);
    }

    @GetMapping
    public List<Buyer> findAll() {
        return buyerUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Buyer findById(@PathVariable Long id) {
        return buyerUseCase.findById(id);
    }

    @PutMapping("/{id}/suspension")
    public Buyer suspend(@PathVariable Long id) {
        return buyerUseCase.suspend(id);
    }

    @PutMapping("/{id}/enablement")
    public Buyer enable(@PathVariable Long id) {
        return buyerUseCase.enable(id);
    }

    @PostMapping("/{id}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Address addAddress(@PathVariable Long id, @Valid @RequestBody Address address) {
        return buyerUseCase.addAddress(id, address);
    }

    @GetMapping("/{id}/addresses")
    public List<Address> listAddresses(@PathVariable Long id) {
        return buyerUseCase.listAddresses(id);
    }

    @GetMapping("/{id}/addresses/primary")
    public Address findPrimaryAddress(@PathVariable Long id) {
        return buyerUseCase.findPrimaryAddress(id);
    }

    @PutMapping("/{id}/addresses/{addressId}/primary")
    public Address setPrimaryAddress(@PathVariable Long id, @PathVariable Long addressId) {
        return buyerUseCase.setPrimaryAddress(id, addressId);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public void removeAddress(@PathVariable Long id, @PathVariable Long addressId) {
        buyerUseCase.removeAddress(id, addressId);
    }
}
