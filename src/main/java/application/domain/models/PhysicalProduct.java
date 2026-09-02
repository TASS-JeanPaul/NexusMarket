package application.domain.models;

import application.domain.enums.ProductType;

public class PhysicalProduct extends Product {
    @Override
    public ProductType getType() {
        return ProductType.PHYSICAL;
    }

    @Override
    public boolean requiresShipping() {
        return true;
    }

    @Override
    public boolean managesInventory() {
        return true;
    }
}
