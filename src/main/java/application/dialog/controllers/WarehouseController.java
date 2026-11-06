package application.dialog.controllers;

import application.data.entities.Warehouse;
import application.models.ports.in.IWarehouseUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

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

@Tag(name = "04 Bodegas", description = "Espacios fisicos de almacenamiento del marketplace y de los vendedores")
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final IWarehouseUseCase warehouseUseCase;

    public WarehouseController(IWarehouseUseCase warehouseUseCase) {
        this.warehouseUseCase = warehouseUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse create(@Valid @RequestBody Warehouse warehouse) {
        return warehouseUseCase.create(warehouse);
    }

    @GetMapping
    public List<Warehouse> findAll(@RequestParam(required = false) Boolean onlyActive,
                                   @RequestParam(required = false) Long sellerId) {
        if (sellerId != null) {
            return warehouseUseCase.findBySeller(sellerId);
        }
        return Boolean.TRUE.equals(onlyActive) ? warehouseUseCase.findActive() : warehouseUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Warehouse findById(@PathVariable Long id) {
        return warehouseUseCase.findById(id);
    }

    @GetMapping("/by-code/{code}")
    public Warehouse findByCode(@PathVariable String code) {
        return warehouseUseCase.findByCode(code);
    }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable Long id, @Valid @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        return warehouseUseCase.update(warehouse);
    }

    @PutMapping("/{id}/activation")
    public Warehouse activate(@PathVariable Long id) {
        return warehouseUseCase.activate(id);
    }

    @PutMapping("/{id}/deactivation")
    public Warehouse deactivate(@PathVariable Long id) {
        return warehouseUseCase.deactivate(id);
    }
}
