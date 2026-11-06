package application.dialog.requests;

import application.data.entities.DigitalProduct;
import application.data.entities.PhysicalProduct;
import application.data.entities.Product;
import application.data.enums.ProductType;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @NotNull ProductType type,
        @NotNull Long sellerId,
        @NotNull Long categoryId) {

    public Product toProduct() {
        Product product = type == ProductType.DIGITAL ? new DigitalProduct() : new PhysicalProduct();
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSellerId(sellerId);
        product.setCategoryId(categoryId);
        return product;
    }
}
