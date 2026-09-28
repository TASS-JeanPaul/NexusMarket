package application.services;

import application.domain.enums.Role;
import application.domain.enums.UserStatus;
import application.domain.exceptions.ResourceNotFoundException;
import application.domain.interfaces.ISellerRepository;
import application.domain.models.Seller;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellerService extends BaseService {

    private final ISellerRepository sellerRepository;
    private final UserService userService;

    public SellerService(ISellerRepository sellerRepository, UserService userService) {
        this.sellerRepository = sellerRepository;
        this.userService = userService;
    }

    public Seller register(Seller seller, Long administratorId) {
        return execute(() -> {
            requireIdentifier(administratorId,
                    "The administrator identifier: sellers cannot register themselves");
            userService.requireRole(administratorId, Role.ADMINISTRATOR);

            require(seller != null, "The seller is required");
            requireText(seller.getFullName(), "The full name");
            requireText(seller.getIdentityDocument(), "The identity document");
            requireText(seller.getEmail(), "The email");
            requireText(seller.getBusinessName(), "The business name");
            requireText(seller.getTaxId(), "The tax identification number");
            require(userService.isEmailAvailable(seller.getEmail()),
                    "The email " + seller.getEmail() + " is already registered");
            require(sellerRepository.findByTaxId(seller.getTaxId()).isEmpty(),
                    "The tax identification number " + seller.getTaxId() + " is already registered");

            seller.setRegisteredById(administratorId);
            seller.setStatus(UserStatus.ACTIVE);
            seller.setRegisteredAt(LocalDateTime.now());
            return sellerRepository.save(seller);
        });
    }

    public Seller findById(Long id) {
        requireIdentifier(id, "The seller identifier");
        return execute(() -> findOrFail(sellerRepository.findById(id), "Seller", id));
    }

    public List<Seller> findAll() {
        return execute(sellerRepository::findAll);
    }

    public Seller findByTaxId(String taxId) {
        requireText(taxId, "The tax identification number");
        return execute(() -> sellerRepository.findByTaxId(taxId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No seller is registered with the tax identification number " + taxId)));
    }

    public Seller requireActive(Long sellerId) {
        Seller seller = findById(sellerId);
        require(seller.getStatus() == UserStatus.ACTIVE,
                "The seller " + sellerId + " is not active and cannot operate");
        return seller;
    }

    public Seller updateBusinessInformation(Long sellerId, String businessName, String phone) {
        return execute(() -> {
            Seller seller = findById(sellerId);
            requireText(businessName, "The business name");

            seller.setBusinessName(businessName);
            seller.setPhone(phone);
            sellerRepository.update(seller);
            return seller;
        });
    }

    public Seller block(Long sellerId) {
        return changeStatus(sellerId, UserStatus.BLOCKED);
    }

    public Seller activate(Long sellerId) {
        return changeStatus(sellerId, UserStatus.ACTIVE);
    }

    private Seller changeStatus(Long sellerId, UserStatus status) {
        return execute(() -> {
            Seller seller = findById(sellerId);
            seller.setStatus(status);
            sellerRepository.update(seller);
            return seller;
        });
    }
}
