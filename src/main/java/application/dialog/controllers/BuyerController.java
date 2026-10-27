package application.dialog.controllers;

import application.data.entities.Address;
import application.data.entities.Buyer;
import application.models.services.BuyerService;

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

@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Buyer register(@RequestBody Buyer buyer) {
        return buyerService.register(buyer);
    }

    @GetMapping
    public List<Buyer> findAll() {
        return buyerService.findAll();
    }

    @GetMapping("/{id}")
    public Buyer findById(@PathVariable Long id) {
        return buyerService.findById(id);
    }

    @PutMapping("/{id}/suspension")
    public Buyer suspend(@PathVariable Long id) {
        return buyerService.suspend(id);
    }

    @PutMapping("/{id}/enablement")
    public Buyer enable(@PathVariable Long id) {
        return buyerService.enable(id);
    }

    @PostMapping("/{id}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Address addAddress(@PathVariable Long id, @RequestBody Address address) {
        return buyerService.addAddress(id, address);
    }

    @GetMapping("/{id}/addresses")
    public List<Address> listAddresses(@PathVariable Long id) {
        return buyerService.listAddresses(id);
    }

    @GetMapping("/{id}/addresses/primary")
    public Address findPrimaryAddress(@PathVariable Long id) {
        return buyerService.findPrimaryAddress(id);
    }

    @PutMapping("/{id}/addresses/{addressId}/primary")
    public Address setPrimaryAddress(@PathVariable Long id, @PathVariable Long addressId) {
        return buyerService.setPrimaryAddress(id, addressId);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public void removeAddress(@PathVariable Long id, @PathVariable Long addressId) {
        buyerService.removeAddress(id, addressId);
    }
}
