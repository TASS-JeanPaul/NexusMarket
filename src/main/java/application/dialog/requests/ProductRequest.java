package application.dialog.requests;

import application.data.entities.DigitalProduct;
import application.data.entities.PhysicalProduct;
import application.data.entities.Product;
import application.data.enums.ProductType;

import java.math.BigDecimal;

public record ProductRequest(
        String code,
        String name,
        String description,
        BigDecimal price,
        ProductType type,
        Long sellerId,
        Long categoryId) {

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
