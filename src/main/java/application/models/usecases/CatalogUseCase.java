package application.models.usecases;

import application.models.ports.in.ICatalogUseCase;
import application.models.services.CatalogService;
import application.data.enums.ProductStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.ICategoryRepository;
import application.data.ports.out.IProductRepository;
import application.data.ports.out.IVariantRepository;
import application.data.entities.Category;
import application.data.entities.Product;
import application.data.entities.Variant;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CatalogUseCase implements ICatalogUseCase {

    private final CatalogService catalogService;

    public CatalogUseCase(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public Category createCategory(Category category) {
        return catalogService.createCategory(category);
    }

    @Override
    public Category findCategoryById(Long id) {
        return catalogService.findCategoryById(id);
    }

    @Override
    public List<Category> findAllCategories() {
        return catalogService.findAllCategories();
    }

    @Override
    public Category updateCategory(Category category) {
        return catalogService.updateCategory(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        return catalogService.deleteCategory(id);
    }

    @Override
    public Product publishProduct(Product product) {
        return catalogService.publishProduct(product);
    }

    @Override
    public Product findProductById(Long id) {
        return catalogService.findProductById(id);
    }

    @Override
    public List<Product> findAllProducts() {
        return catalogService.findAllProducts();
    }

    @Override
    public List<Product> findPublishedProducts() {
        return catalogService.findPublishedProducts();
    }

    @Override
    public List<Product> findProductsBySeller(Long sellerId) {
        return catalogService.findProductsBySeller(sellerId);
    }

    @Override
    public List<Product> findProductsByCategory(Long categoryId) {
        return catalogService.findProductsByCategory(categoryId);
    }

    @Override
    public Product findProductByCode(String code) {
        return catalogService.findProductByCode(code);
    }

    @Override
    public Product requirePublished(Long productId) {
        return catalogService.requirePublished(productId);
    }

    @Override
    public Product updateProductInformation(Long productId, String name, String description) {
        return catalogService.updateProductInformation(productId, name, description);
    }

    @Override
    public Product updatePrice(Long productId, BigDecimal price) {
        return catalogService.updatePrice(productId, price);
    }

    @Override
    public Product suspendProduct(Long productId) {
        return catalogService.suspendProduct(productId);
    }

    @Override
    public Product republishProduct(Long productId) {
        return catalogService.republishProduct(productId);
    }

    @Override
    public Product discontinueProduct(Long productId) {
        return catalogService.discontinueProduct(productId);
    }

    @Override
    public Variant addVariant(Long productId, Variant variant) {
        return catalogService.addVariant(productId, variant);
    }

    @Override
    public List<Variant> findVariantsByProduct(Long productId) {
        return catalogService.findVariantsByProduct(productId);
    }

    @Override
    public Variant findVariantById(Long variantId) {
        return catalogService.findVariantById(variantId);
    }

    @Override
    public boolean removeVariant(Long productId, Long variantId) {
        return catalogService.removeVariant(productId, variantId);
    }

    @Override
    public BigDecimal resolvePrice(Long productId, Long variantId) {
        return catalogService.resolvePrice(productId, variantId);
    }
}
