package application.data.entities;

import application.data.enums.ProductType;

public class DigitalProduct extends Product {
    @Override
    public ProductType getType() {
        return ProductType.DIGITAL;
    }

    @Override
    public boolean requiresShipping() {
        return false;
    }

    @Override
    public boolean managesInventory() {
        return false;
    }
}
