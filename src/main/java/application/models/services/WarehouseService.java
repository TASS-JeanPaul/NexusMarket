package application.models.services;

import application.data.enums.WarehouseType;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IWarehouseRepository;
import application.data.entities.Warehouse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService extends BaseService {

    private final IWarehouseRepository warehouseRepository;
    private final SellerService sellerService;

    public WarehouseService(IWarehouseRepository warehouseRepository, SellerService sellerService) {
        this.warehouseRepository = warehouseRepository;
        this.sellerService = sellerService;
    }

    public Warehouse create(Warehouse warehouse) {
        return execute(() -> {
            require(warehouse != null, "The warehouse is required");
            requireText(warehouse.getCode(), "The warehouse code");
            requireText(warehouse.getName(), "The warehouse name");
            requireText(warehouse.getCity(), "The city");
            require(warehouse.getType() != null, "The warehouse type is required");
            require(warehouseRepository.findByCode(warehouse.getCode()).isEmpty(),
                    "The warehouse code " + warehouse.getCode() + " is already in use");

            validateOwnership(warehouse);

            warehouse.setActive(true);
            return warehouseRepository.save(warehouse);
        });
    }

    public Warehouse findById(Long id) {
        requireIdentifier(id, "The warehouse identifier");
        return execute(() -> findOrFail(warehouseRepository.findById(id), "Warehouse", id));
    }

    public List<Warehouse> findAll() {
        return execute(warehouseRepository::findAll);
    }

    public List<Warehouse> findActive() {
        return execute(warehouseRepository::findActive);
    }

    public List<Warehouse> findBySeller(Long sellerId) {
        requireIdentifier(sellerId, "The seller identifier");
        return execute(() -> warehouseRepository.findBySellerId(sellerId));
    }

    public Warehouse findByCode(String code) {
        requireText(code, "The warehouse code");
        return execute(() -> warehouseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No warehouse is registered with the code " + code)));
    }

    public Warehouse requireActive(Long warehouseId) {
        Warehouse warehouse = findById(warehouseId);
        require(warehouse.isActive(),
                "The warehouse " + warehouseId + " is not active and cannot store merchandise");
        return warehouse;
    }

    public Warehouse update(Warehouse warehouse) {
        return execute(() -> {
            requireIdentifier(warehouse.getId(), "The warehouse identifier");
            findById(warehouse.getId());
            requireText(warehouse.getName(), "The warehouse name");
            requireText(warehouse.getCity(), "The city");
            validateOwnership(warehouse);

            warehouseRepository.update(warehouse);
            return warehouse;
        });
    }

    public Warehouse activate(Long id) {
        return changeActivation(id, true);
    }

    public Warehouse deactivate(Long id) {
        return changeActivation(id, false);
    }

    private Warehouse changeActivation(Long id, boolean active) {
        return execute(() -> {
            Warehouse warehouse = findById(id);
            warehouse.setActive(active);
            warehouseRepository.update(warehouse);
            return warehouse;
        });
    }

    private void validateOwnership(Warehouse warehouse) {
        if (warehouse.getType() == WarehouseType.SELLER) {
            requireIdentifier(warehouse.getSellerId(),
                    "The owning seller identifier of a seller warehouse");
            sellerService.requireActive(warehouse.getSellerId());
        } else {
            require(warehouse.getSellerId() == null,
                    "A marketplace warehouse cannot belong to a seller");
        }
    }
}
