package application.domain.models;

import application.domain.enums.WarehouseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Warehouse {
    private Long id;
    private String code;
    private String name;
    private String street;
    private String city;
    private WarehouseType type;
    private Long sellerId;
    private boolean active;
}
