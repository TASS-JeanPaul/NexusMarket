package application.models.ports.in;

import application.data.enums.WarehouseType;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IWarehouseRepository;
import application.data.entities.Warehouse;
import java.util.List;

public interface IWarehouseUseCase {

    Warehouse create(Warehouse warehouse);

    Warehouse findById(Long id);

    List<Warehouse> findAll();

    List<Warehouse> findActive();

    List<Warehouse> findBySeller(Long sellerId);

    Warehouse findByCode(String code);

    Warehouse requireActive(Long warehouseId);

    Warehouse update(Warehouse warehouse);

    Warehouse activate(Long id);

    Warehouse deactivate(Long id);
}
