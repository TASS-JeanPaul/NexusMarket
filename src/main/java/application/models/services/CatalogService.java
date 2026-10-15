package application.models.services;

import application.data.enums.ProductStatus;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.ICategoryRepository;
import application.data.contracts.IProductRepository;
import application.data.contracts.IVariantRepository;
import application.data.entities.Category;
import application.data.entities.Product;
import application.data.entities.Variant;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CatalogService extends BaseService {

    private final IProductRepository productRepository;
    private final IVariantRepository variantRepository;
    private final ICategoryRepository categoryRepository;
    private final SellerService sellerService;

    public CatalogService(IProductRepository productRepository,
                          IVariantRepository variantRepository,
                          ICategoryRepository categoryRepository,
                          SellerService sellerService) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository;
        this.sellerService = sellerService;
    }

    public Category createCategory(Category category) {
        return execute(() -> {
            require(category != null, "The category is required");
            requireText(category.getName(), "The category name");
            require(categoryRepository.findByName(category.getName()).isEmpty(),
                    "The category " + category.getName() + " already exists");

            return categoryRepository.save(category);
        });
    }

    public Category findCategoryById(Long id) {
        requireIdentifier(id, "The category identifier");
        return execute(() -> findOrFail(categoryRepository.findById(id), "Category", id));
    }

    public List<Category> findAllCategories() {
        return execute(categoryRepository::findAll);
    }

    public Category updateCategory(Category category) {
        return execute(() -> {
            requireIdentifier(category.getId(), "The category identifier");
            findCategoryById(category.getId());
            requireText(category.getName(), "The category name");
            require(categoryRepository.findByName(category.getName())
                            .filter(other -> !other.getId().equals(category.getId()))
                            .isEmpty(),
                    "The category name " + category.getName() + " belongs to another category");

            categoryRepository.update(category);
            return category;
        });
    }

    public boolean deleteCategory(Long id) {
        return execute(() -> {
            findCategoryById(id);
            require(productRepository.findByCategoryId(id).isEmpty(),
                    "The category " + id + " cannot be deleted because it still groups products");

            return categoryRepository.delete(id);
        });
    }

    public Product publishProduct(Product product) {
        return execute(() -> {
            require(product != null, "The product is required");
            requireText(product.getCode(), "The product code");
            requireText(product.getName(), "The product name");
            requirePositiveAmount(product.getPrice(), "The product price");
            requireIdentifier(product.getSellerId(), "The seller identifier");
            requireIdentifier(product.getCategoryId(), "The category identifier");

            sellerService.requireActive(product.getSellerId());
            findCategoryById(product.getCategoryId());
            require(productRepository.findByCode(product.getCode()).isEmpty(),
                    "The product code " + product.getCode() + " is already in use");

            product.setStatus(ProductStatus.PUBLISHED);
            product.setCreatedAt(LocalDateTime.now());
            return productRepository.save(product);
        });
    }

    public Product findProductById(Long id) {
        requireIdentifier(id, "The product identifier");
        return execute(() -> findOrFail(productRepository.findById(id), "Product", id));
    }

    public List<Product> findAllProducts() {
        return execute(productRepository::findAll);
    }

    public List<Product> findPublishedProducts() {
        return execute(() -> productRepository.findByStatus(ProductStatus.PUBLISHED));
    }

    public List<Product> findProductsBySeller(Long sellerId) {
        requireIdentifier(sellerId, "The seller identifier");
        return execute(() -> productRepository.findBySellerId(sellerId));
    }

    public List<Product> findProductsByCategory(Long categoryId) {
        findCategoryById(categoryId);
        return execute(() -> productRepository.findByCategoryId(categoryId));
    }

    public Product findProductByCode(String code) {
        requireText(code, "The product code");
        return execute(() -> productRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No product is registered with the code " + code)));
    }

    public Product requirePublished(Long productId) {
        Product product = findProductById(productId);
        require(product.getStatus() == ProductStatus.PUBLISHED,
                "The product " + productId + " is not published and cannot be sold");
        return product;
    }

    public Product updateProductInformation(Long productId, String name, String description) {
        return execute(() -> {
            Product product = findProductById(productId);
            requireModifiable(product);
            requireText(name, "The product name");

            product.setName(name);
            product.setDescription(description);
            productRepository.update(product);
            return product;
        });
    }

    public Product updatePrice(Long productId, BigDecimal price) {
        return execute(() -> {
            Product product = findProductById(productId);
            requireModifiable(product);
            requirePositiveAmount(price, "The product price");

            product.setPrice(price);
            productRepository.update(product);
            return product;
        });
    }

    public Product suspendProduct(Long productId) {
        return changeProductStatus(productId, ProductStatus.SUSPENDED);
    }

    public Product republishProduct(Long productId) {
        return changeProductStatus(productId, ProductStatus.PUBLISHED);
    }

    public Product discontinueProduct(Long productId) {
        return changeProductStatus(productId, ProductStatus.DISCONTINUED);
    }

    public Variant addVariant(Long productId, Variant variant) {
        return execute(() -> {
            Product product = findProductById(productId);
            requireModifiable(product);
            require(variant != null, "The variant is required");
            requireText(variant.getSku(), "The variant SKU");
            requireText(variant.getName(), "The variant name");
            requireText(variant.getValue(), "The variant value");
            require(variantRepository.findBySku(variant.getSku()).isEmpty(),
                    "The SKU " + variant.getSku() + " is already in use");

            if (variant.getAdditionalPrice() == null) {
                variant.setAdditionalPrice(BigDecimal.ZERO);
            }
            require(variant.getAdditionalPrice().compareTo(BigDecimal.ZERO) >= 0,
                    "The additional price of a variant cannot be negative");

            variant.setProductId(productId);
            return variantRepository.save(variant);
        });
    }

    public List<Variant> findVariantsByProduct(Long productId) {
        findProductById(productId);
        return execute(() -> variantRepository.findByProductId(productId));
    }

    public Variant findVariantById(Long variantId) {
        requireIdentifier(variantId, "The variant identifier");
        return execute(() -> findOrFail(variantRepository.findById(variantId), "Variant", variantId));
    }

    public boolean removeVariant(Long productId, Long variantId) {
        return execute(() -> {
            Variant variant = findVariantById(variantId);
            require(productId.equals(variant.getProductId()),
                    "The variant " + variantId + " does not belong to the product " + productId);

            return variantRepository.delete(variantId);
        });
    }

    public BigDecimal resolvePrice(Long productId, Long variantId) {
        Product product = findProductById(productId);

        if (variantId == null) {
            return product.getPrice();
        }

        Variant variant = findVariantById(variantId);
        require(productId.equals(variant.getProductId()),
                "The variant " + variantId + " does not belong to the product " + productId);

        BigDecimal additionalPrice = variant.getAdditionalPrice() == null
                ? BigDecimal.ZERO
                : variant.getAdditionalPrice();
        return product.getPrice().add(additionalPrice);
    }

    private Product changeProductStatus(Long productId, ProductStatus status) {
        return execute(() -> {
            Product product = findProductById(productId);
            require(product.getStatus() != ProductStatus.DISCONTINUED,
                    "A discontinued product cannot change its status again");

            product.setStatus(status);
            productRepository.update(product);
            return product;
        });
    }

    private void requireModifiable(Product product) {
        require(product.getStatus() != ProductStatus.DISCONTINUED,
                "A discontinued product cannot be modified");
    }
}
