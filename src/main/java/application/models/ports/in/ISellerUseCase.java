package application.models.ports.in;

import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.ISellerRepository;
import application.data.entities.Seller;
import application.models.ports.out.IPasswordHasher;
import java.time.LocalDateTime;
import java.util.List;

public interface ISellerUseCase {

    Seller register(Seller seller, Long administratorId);

    Seller findById(Long id);

    List<Seller> findAll();

    Seller findByTaxId(String taxId);

    Seller requireActive(Long sellerId);

    Seller updateBusinessInformation(Long sellerId, String businessName, String phone);

    Seller block(Long sellerId);

    Seller activate(Long sellerId);
}
