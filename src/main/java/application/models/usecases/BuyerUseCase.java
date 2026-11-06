package application.models.usecases;

import application.models.ports.in.IBuyerUseCase;
import application.models.services.BuyerService;
import application.data.enums.CommercialStatus;
import application.data.enums.UserStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IAddressRepository;
import application.data.ports.out.IBuyerRepository;
import application.data.entities.Address;
import application.data.entities.Buyer;
import application.models.ports.out.IPasswordHasher;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BuyerUseCase implements IBuyerUseCase {

    private final BuyerService buyerService;

    public BuyerUseCase(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Override
    public Buyer register(Buyer buyer) {
        return buyerService.register(buyer);
    }

    @Override
    public Buyer findById(Long id) {
        return buyerService.findById(id);
    }

    @Override
    public List<Buyer> findAll() {
        return buyerService.findAll();
    }

    @Override
    public Buyer requirePurchaseEnabled(Long buyerId) {
        return buyerService.requirePurchaseEnabled(buyerId);
    }

    @Override
    public Buyer suspend(Long buyerId) {
        return buyerService.suspend(buyerId);
    }

    @Override
    public Buyer enable(Long buyerId) {
        return buyerService.enable(buyerId);
    }

    @Override
    public Address addAddress(Long buyerId, Address address) {
        return buyerService.addAddress(buyerId, address);
    }

    @Override
    public List<Address> listAddresses(Long buyerId) {
        return buyerService.listAddresses(buyerId);
    }

    @Override
    public Address findPrimaryAddress(Long buyerId) {
        return buyerService.findPrimaryAddress(buyerId);
    }

    @Override
    public Address setPrimaryAddress(Long buyerId, Long addressId) {
        return buyerService.setPrimaryAddress(buyerId, addressId);
    }

    @Override
    public boolean removeAddress(Long buyerId, Long addressId) {
        return buyerService.removeAddress(buyerId, addressId);
    }

    @Override
    public Address requireDeliveryAddress(Long buyerId, Long addressId) {
        return buyerService.requireDeliveryAddress(buyerId, addressId);
    }
}
