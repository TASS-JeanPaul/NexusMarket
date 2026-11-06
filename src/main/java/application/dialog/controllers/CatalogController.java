package application.dialog.controllers;

import application.data.entities.Category;
import application.data.entities.Product;
import application.data.entities.Variant;
import application.dialog.requests.ProductInformationRequest;
import application.dialog.requests.ProductRequest;
import application.models.ports.in.ICatalogUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "05 Catalogo", description = "Categorias, productos fisicos y digitales, y variantes")
@RestController
@RequestMapping("/api")
public class CatalogController {

    private final ICatalogUseCase catalogUseCase;

    public CatalogController(ICatalogUseCase catalogUseCase) {
        this.catalogUseCase = catalogUseCase;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody Category category) {
        return catalogUseCase.createCategory(category);
    }

    @GetMapping("/categories")
    public List<Category> findAllCategories() {
        return catalogUseCase.findAllCategories();
    }

    @GetMapping("/categories/{id}")
    public Category findCategoryById(@PathVariable Long id) {
        return catalogUseCase.findCategoryById(id);
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return catalogUseCase.updateCategory(category);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        catalogUseCase.deleteCategory(id);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product publishProduct(@RequestBody ProductRequest request) {
        return catalogUseCase.publishProduct(request.toProduct());
    }

    @GetMapping("/products")
    public List<Product> findProducts(@RequestParam(required = false) Boolean onlyPublished,
                                      @RequestParam(required = false) Long sellerId,
                                      @RequestParam(required = false) Long categoryId) {
        if (sellerId != null) {
            return catalogUseCase.findProductsBySeller(sellerId);
        }
        if (categoryId != null) {
            return catalogUseCase.findProductsByCategory(categoryId);
        }
        return Boolean.TRUE.equals(onlyPublished)
                ? catalogUseCase.findPublishedProducts()
                : catalogUseCase.findAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product findProductById(@PathVariable Long id) {
        return catalogUseCase.findProductById(id);
    }

    @GetMapping("/products/by-code/{code}")
    public Product findProductByCode(@PathVariable String code) {
        return catalogUseCase.findProductByCode(code);
    }

    @PutMapping("/products/{id}/information")
    public Product updateProductInformation(@PathVariable Long id,
                                            @RequestBody ProductInformationRequest request) {
        return catalogUseCase.updateProductInformation(id, request.name(), request.description());
    }

    @PutMapping("/products/{id}/price")
    public Product updatePrice(@PathVariable Long id, @RequestParam BigDecimal value) {
        return catalogUseCase.updatePrice(id, value);
    }

    @PutMapping("/products/{id}/suspension")
    public Product suspendProduct(@PathVariable Long id) {
        return catalogUseCase.suspendProduct(id);
    }

    @PutMapping("/products/{id}/publication")
    public Product republishProduct(@PathVariable Long id) {
        return catalogUseCase.republishProduct(id);
    }

    @PutMapping("/products/{id}/discontinuation")
    public Product discontinueProduct(@PathVariable Long id) {
        return catalogUseCase.discontinueProduct(id);
    }

    @PostMapping("/products/{id}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public Variant addVariant(@PathVariable Long id, @RequestBody Variant variant) {
        return catalogUseCase.addVariant(id, variant);
    }

    @GetMapping("/products/{id}/variants")
    public List<Variant> findVariants(@PathVariable Long id) {
        return catalogUseCase.findVariantsByProduct(id);
    }

    @DeleteMapping("/products/{id}/variants/{variantId}")
    public void removeVariant(@PathVariable Long id, @PathVariable Long variantId) {
        catalogUseCase.removeVariant(id, variantId);
    }
}
