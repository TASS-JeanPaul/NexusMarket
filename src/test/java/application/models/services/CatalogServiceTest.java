package application.models.services;

import application.data.entities.Category;
import application.data.entities.PhysicalProduct;
import application.data.entities.Product;
import application.data.enums.ProductStatus;
import application.data.ports.out.ICategoryRepository;
import application.data.ports.out.IProductRepository;
import application.data.ports.out.IVariantRepository;
import application.shared.exceptions.BusinessRuleException;
import application.shared.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private IProductRepository productRepository;
    @Mock
    private IVariantRepository variantRepository;
    @Mock
    private ICategoryRepository categoryRepository;
    @Mock
    private SellerService sellerService;

    private CatalogService service;

    @BeforeEach
    void setUp() {
        service = new CatalogService(productRepository, variantRepository, categoryRepository, sellerService);
    }

    private Product product() {
        Product product = new PhysicalProduct();
        product.setCode("P-1");
        product.setName("Mouse");
        product.setPrice(new BigDecimal("15.00"));
        product.setSellerId(1L);
        product.setCategoryId(2L);
        return product;
    }

    @Test
    void rejectsPublishingAProductWithADuplicatedCode() throws Exception {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(productRepository.findByCode("P-1")).thenReturn(Optional.of(product()));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.publishProduct(product()));

        assertTrue(error.getMessage().contains("already in use"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void publishesAProductWithAnUnusedCode() throws Exception {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));
        when(productRepository.findByCode("P-1")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        Product result = service.publishProduct(product());

        assertEquals(ProductStatus.PUBLISHED, result.getStatus());
        verify(sellerService).requireActive(1L);
    }

    @Test
    void rejectsPublishingAProductWithANonPositivePrice() throws Exception {
        Product free = product();
        free.setPrice(BigDecimal.ZERO);

        assertThrows(BusinessRuleException.class, () -> service.publishProduct(free));
        verify(productRepository, never()).save(any());
    }

    @Test
    void rejectsPublishingAProductForAMissingCategory() throws Exception {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.publishProduct(product()));
    }

    @Test
    void rejectsChangingTheStatusOfADiscontinuedProduct() throws Exception {
        Product discontinued = product();
        discontinued.setStatus(ProductStatus.DISCONTINUED);
        when(productRepository.findById(5L)).thenReturn(Optional.of(discontinued));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.republishProduct(5L));

        assertTrue(error.getMessage().contains("discontinued"));
    }

    @Test
    void rejectsCreatingADuplicatedCategory() throws Exception {
        Category category = new Category();
        category.setName("Audio");
        when(categoryRepository.findByName("Audio")).thenReturn(Optional.of(new Category()));

        assertThrows(BusinessRuleException.class, () -> service.createCategory(category));
    }
}
