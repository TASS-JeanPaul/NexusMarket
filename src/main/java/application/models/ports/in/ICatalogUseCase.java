package application.models.ports.in;

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

public interface ICatalogUseCase {

    Category createCategory(Category category);

    Category findCategoryById(Long id);

    List<Category> findAllCategories();

    Category updateCategory(Category category);

    boolean deleteCategory(Long id);

    Product publishProduct(Product product);

    Product findProductById(Long id);

    List<Product> findAllProducts();

    List<Product> findPublishedProducts();

    List<Product> findProductsBySeller(Long sellerId);

    List<Product> findProductsByCategory(Long categoryId);

    Product findProductByCode(String code);

    Product requirePublished(Long productId);

    Product updateProductInformation(Long productId, String name, String description);

    Product updatePrice(Long productId, BigDecimal price);

    Product suspendProduct(Long productId);

    Product republishProduct(Long productId);

    Product discontinueProduct(Long productId);

    Variant addVariant(Long productId, Variant variant);

    List<Variant> findVariantsByProduct(Long productId);

    Variant findVariantById(Long variantId);

    boolean removeVariant(Long productId, Long variantId);

    BigDecimal resolvePrice(Long productId, Long variantId);
}
