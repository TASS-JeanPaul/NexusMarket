package application.dialog.controllers;

import application.data.entities.Warehouse;
import application.models.services.WarehouseService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse create(@RequestBody Warehouse warehouse) {
        return warehouseService.create(warehouse);
    }

    @GetMapping
    public List<Warehouse> findAll(@RequestParam(required = false) Boolean onlyActive,
                                   @RequestParam(required = false) Long sellerId) {
        if (sellerId != null) {
            return warehouseService.findBySeller(sellerId);
        }
        return Boolean.TRUE.equals(onlyActive) ? warehouseService.findActive() : warehouseService.findAll();
    }

    @GetMapping("/{id}")
    public Warehouse findById(@PathVariable Long id) {
        return warehouseService.findById(id);
    }

    @GetMapping("/by-code/{code}")
    public Warehouse findByCode(@PathVariable String code) {
        return warehouseService.findByCode(code);
    }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        return warehouseService.update(warehouse);
    }

    @PutMapping("/{id}/activation")
    public Warehouse activate(@PathVariable Long id) {
        return warehouseService.activate(id);
    }

    @PutMapping("/{id}/deactivation")
    public Warehouse deactivate(@PathVariable Long id) {
        return warehouseService.deactivate(id);
    }
}
