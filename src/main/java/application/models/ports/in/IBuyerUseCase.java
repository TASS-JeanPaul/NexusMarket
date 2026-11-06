package application.models.ports.in;

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

public interface IBuyerUseCase {

    Buyer register(Buyer buyer);

    Buyer findById(Long id);

    List<Buyer> findAll();

    Buyer requirePurchaseEnabled(Long buyerId);

    Buyer suspend(Long buyerId);

    Buyer enable(Long buyerId);

    Address addAddress(Long buyerId, Address address);

    List<Address> listAddresses(Long buyerId);

    Address findPrimaryAddress(Long buyerId);

    Address setPrimaryAddress(Long buyerId, Long addressId);

    boolean removeAddress(Long buyerId, Long addressId);

    Address requireDeliveryAddress(Long buyerId, Long addressId);
}
