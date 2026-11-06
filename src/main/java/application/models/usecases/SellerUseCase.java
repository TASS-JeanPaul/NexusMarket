package application.models.usecases;

import application.models.ports.in.ISellerUseCase;
import application.models.services.SellerService;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.ISellerRepository;
import application.data.entities.Seller;
import application.models.ports.out.IPasswordHasher;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SellerUseCase implements ISellerUseCase {

    private final SellerService sellerService;

    public SellerUseCase(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public Seller register(Seller seller, Long administratorId) {
        return sellerService.register(seller, administratorId);
    }

    @Override
    public Seller findById(Long id) {
        return sellerService.findById(id);
    }

    @Override
    public List<Seller> findAll() {
        return sellerService.findAll();
    }

    @Override
    public Seller findByTaxId(String taxId) {
        return sellerService.findByTaxId(taxId);
    }

    @Override
    public Seller requireActive(Long sellerId) {
        return sellerService.requireActive(sellerId);
    }

    @Override
    public Seller updateBusinessInformation(Long sellerId, String businessName, String phone) {
        return sellerService.updateBusinessInformation(sellerId, businessName, phone);
    }

    @Override
    public Seller block(Long sellerId) {
        return sellerService.block(sellerId);
    }

    @Override
    public Seller activate(Long sellerId) {
        return sellerService.activate(sellerId);
    }
}
