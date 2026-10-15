package application.models.services;

import application.data.enums.CommercialStatus;
import application.data.enums.UserStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IAddressRepository;
import application.data.contracts.IBuyerRepository;
import application.data.entities.Address;
import application.data.entities.Buyer;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuyerService extends BaseService {

    private final IBuyerRepository buyerRepository;
    private final IAddressRepository addressRepository;
    private final UserService userService;

    public BuyerService(IBuyerRepository buyerRepository,
                        IAddressRepository addressRepository,
                        UserService userService) {
        this.buyerRepository = buyerRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public Buyer register(Buyer buyer) {
        return execute(() -> {
            require(buyer != null, "The buyer is required");
            requireText(buyer.getFullName(), "The full name");
            requireText(buyer.getIdentityDocument(), "The identity document");
            requireText(buyer.getEmail(), "The email");
            require(userService.isEmailAvailable(buyer.getEmail()),
                    "The email " + buyer.getEmail() + " is already registered");

            buyer.setStatus(UserStatus.ACTIVE);
            buyer.setCommercialStatus(CommercialStatus.ENABLED);
            buyer.setRegisteredAt(LocalDateTime.now());
            return buyerRepository.save(buyer);
        });
    }

    public Buyer findById(Long id) {
        requireIdentifier(id, "The buyer identifier");
        return execute(() -> findOrFail(buyerRepository.findById(id), "Buyer", id));
    }

    public List<Buyer> findAll() {
        return execute(buyerRepository::findAll);
    }

    public Buyer requirePurchaseEnabled(Long buyerId) {
        Buyer buyer = findById(buyerId);
        require(buyer.getStatus() == UserStatus.ACTIVE,
                "The buyer " + buyerId + " is not active");
        require(buyer.getCommercialStatus() == CommercialStatus.ENABLED,
                "The buyer " + buyerId + " is commercially suspended and cannot place orders");
        return buyer;
    }

    public Buyer suspend(Long buyerId) {
        return changeCommercialStatus(buyerId, CommercialStatus.SUSPENDED);
    }

    public Buyer enable(Long buyerId) {
        return changeCommercialStatus(buyerId, CommercialStatus.ENABLED);
    }

    public Address addAddress(Long buyerId, Address address) {
        return execute(() -> {
            findById(buyerId);
            require(address != null, "The address is required");
            requireText(address.getStreet(), "The street");
            requireText(address.getCity(), "The city");

            List<Address> registered = addressRepository.findByBuyerId(buyerId);
            address.setBuyerId(buyerId);

            if (registered.isEmpty()) {
                address.setPrimary(true);
            } else if (address.isPrimary()) {
                clearPrimaryFlag(registered);
            }
            return addressRepository.save(address);
        });
    }

    public List<Address> listAddresses(Long buyerId) {
        findById(buyerId);
        return execute(() -> addressRepository.findByBuyerId(buyerId));
    }

    public Address findPrimaryAddress(Long buyerId) {
        findById(buyerId);
        return execute(() -> addressRepository.findPrimaryByBuyerId(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The buyer " + buyerId + " has no primary address registered")));
    }

    public Address setPrimaryAddress(Long buyerId, Long addressId) {
        return execute(() -> {
            Address address = findAddressOfBuyer(buyerId, addressId);
            clearPrimaryFlag(addressRepository.findByBuyerId(buyerId));

            address.setPrimary(true);
            addressRepository.update(address);
            return address;
        });
    }

    public boolean removeAddress(Long buyerId, Long addressId) {
        return execute(() -> {
            Address address = findAddressOfBuyer(buyerId, addressId);
            require(!address.isPrimary(),
                    "The primary address cannot be removed; designate another one first");
            return addressRepository.delete(addressId);
        });
    }

    public Address requireDeliveryAddress(Long buyerId, Long addressId) {
        return findAddressOfBuyer(buyerId, addressId);
    }

    private Buyer changeCommercialStatus(Long buyerId, CommercialStatus status) {
        return execute(() -> {
            Buyer buyer = findById(buyerId);
            buyer.setCommercialStatus(status);
            buyerRepository.update(buyer);
            return buyer;
        });
    }

    private Address findAddressOfBuyer(Long buyerId, Long addressId) {
        requireIdentifier(addressId, "The address identifier");
        return execute(() -> {
            Address address = findOrFail(addressRepository.findById(addressId), "Address", addressId);
            require(buyerId.equals(address.getBuyerId()),
                    "The address " + addressId + " does not belong to the buyer " + buyerId);
            return address;
        });
    }

    private void clearPrimaryFlag(List<Address> addresses) {
        execute(() -> {
            for (Address current : addresses) {
                if (current.isPrimary()) {
                    current.setPrimary(false);
                    addressRepository.update(current);
                }
            }
            return true;
        });
    }
}
