package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.data.enums.ProductStatus;
import application.data.enums.ProductType;

@Getter
@Setter
public abstract class Product {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductStatus status;
    private Long sellerId;
    private Long categoryId;
    private List<Variant> variants = new ArrayList<>();
    private LocalDateTime createdAt;

    public abstract ProductType getType();

    public abstract boolean requiresShipping();

    public abstract boolean managesInventory();
}
