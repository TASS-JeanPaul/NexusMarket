package application.models.usecases;

import application.models.ports.in.IWarehouseUseCase;
import application.models.services.WarehouseService;
import application.data.enums.WarehouseType;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IWarehouseRepository;
import application.data.entities.Warehouse;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WarehouseUseCase implements IWarehouseUseCase {

    private final WarehouseService warehouseService;

    public WarehouseUseCase(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        return warehouseService.create(warehouse);
    }

    @Override
    public Warehouse findById(Long id) {
        return warehouseService.findById(id);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseService.findAll();
    }

    @Override
    public List<Warehouse> findActive() {
        return warehouseService.findActive();
    }

    @Override
    public List<Warehouse> findBySeller(Long sellerId) {
        return warehouseService.findBySeller(sellerId);
    }

    @Override
    public Warehouse findByCode(String code) {
        return warehouseService.findByCode(code);
    }

    @Override
    public Warehouse requireActive(Long warehouseId) {
        return warehouseService.requireActive(warehouseId);
    }

    @Override
    public Warehouse update(Warehouse warehouse) {
        return warehouseService.update(warehouse);
    }

    @Override
    public Warehouse activate(Long id) {
        return warehouseService.activate(id);
    }

    @Override
    public Warehouse deactivate(Long id) {
        return warehouseService.deactivate(id);
    }
}
