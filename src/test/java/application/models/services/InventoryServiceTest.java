package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.DigitalProduct;
import application.data.entities.Inventory;
import application.data.entities.InventoryMovement;
import application.data.entities.PhysicalProduct;
import application.data.enums.InventoryStatus;
import application.data.enums.MovementType;
import application.data.ports.out.IInventoryMovementRepository;
import application.data.ports.out.IInventoryRepository;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private static final Long PRODUCT = 1L;
    private static final Long WAREHOUSE = 2L;
    private static final Long USER = 3L;

    @Mock
    private IInventoryRepository inventoryRepository;
    @Mock
    private IInventoryMovementRepository movementRepository;
    @Mock
    private CatalogService catalogService;
    @Mock
    private WarehouseService warehouseService;
    @Mock
    private UserService userService;

    private MockedStatic<TransactionManager> transactions;
    private InventoryService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new InventoryService(inventoryRepository, movementRepository, catalogService,
                warehouseService, userService);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    private Inventory stock(int available, int reserved) throws Exception {
        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setProductId(PRODUCT);
        inventory.setWarehouseId(WAREHOUSE);
        inventory.setAvailableQuantity(available);
        inventory.setReservedQuantity(reserved);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        when(inventoryRepository.findByProductVariantAndWarehouse(PRODUCT, null, WAREHOUSE))
                .thenReturn(Optional.of(inventory));
        return inventory;
    }

    @Test
    void rejectsReservingMoreUnitsThanAvailable() throws Exception {
        Inventory inventory = stock(5, 0);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.reserve(PRODUCT, null, WAREHOUSE, 6, USER));

        assertTrue(error.getMessage().contains("Insufficient stock"));
        assertEquals(5, inventory.getAvailableQuantity());
        verify(inventoryRepository, never()).update(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void reservesUnitsMovingThemFromAvailableToReserved() throws Exception {
        Inventory inventory = stock(5, 1);

        Inventory result = service.reserve(PRODUCT, null, WAREHOUSE, 2, USER);

        assertEquals(3, result.getAvailableQuantity());
        assertEquals(3, inventory.getReservedQuantity());
        verify(inventoryRepository).update(inventory);
    }

    @Test
    void rejectsReservingDamagedInventory() throws Exception {
        Inventory inventory = stock(5, 0);
        inventory.setStatus(InventoryStatus.DAMAGED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.reserve(PRODUCT, null, WAREHOUSE, 1, USER));

        assertTrue(error.getMessage().contains("damaged"));
    }

    @Test
    void rejectsAdjustingToANegativeQuantity() throws Exception {
        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.adjust(PRODUCT, null, WAREHOUSE, -1, USER, "count"));

        assertTrue(error.getMessage().contains("cannot be negative"));
        verify(inventoryRepository, never()).update(any());
    }

    @Test
    void rejectsRegisteringNegativeInitialQuantity() throws Exception {
        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(PRODUCT, null, WAREHOUSE, -5, USER));

        assertTrue(error.getMessage().contains("cannot be negative"));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void rejectsReleasingMoreUnitsThanReserved() throws Exception {
        stock(5, 2);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.release(PRODUCT, null, WAREHOUSE, 3, USER));

        assertTrue(error.getMessage().contains("cannot be released"));
    }

    @Test
    void rejectsMarkingMoreDamagedUnitsThanAvailable() throws Exception {
        stock(2, 0);

        assertThrows(BusinessRuleException.class,
                () -> service.markAsDamaged(PRODUCT, null, WAREHOUSE, 3, USER));
        verify(inventoryRepository, never()).update(any());
    }

    @Test
    void recordsAMovementWhenReserving() throws Exception {
        stock(5, 0);

        service.reserve(PRODUCT, null, WAREHOUSE, 2, USER);

        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(captor.capture());
        InventoryMovement movement = captor.getValue();
        assertEquals(MovementType.RESERVATION, movement.getType());
        assertEquals(2, movement.getQuantity());
        assertEquals(USER, movement.getUserId());
        assertEquals(10L, movement.getInventoryId());
    }

    @Test
    void recordsAMovementWhenRestocking() throws Exception {
        Inventory inventory = stock(5, 0);

        service.restock(PRODUCT, null, WAREHOUSE, 4, USER);

        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(captor.capture());
        assertEquals(MovementType.INBOUND, captor.getValue().getType());
        assertEquals(4, captor.getValue().getQuantity());
        assertEquals(9, inventory.getAvailableQuantity());
    }

    @Test
    void recordsAMovementWhenAdjustingWithTheAbsoluteDifference() throws Exception {
        Inventory inventory = stock(10, 0);

        service.adjust(PRODUCT, null, WAREHOUSE, 7, USER, "physical count");

        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(captor.capture());
        assertEquals(MovementType.ADJUSTMENT, captor.getValue().getType());
        assertEquals(3, captor.getValue().getQuantity());
        assertEquals("physical count", captor.getValue().getNotes());
        assertEquals(7, inventory.getAvailableQuantity());
    }

    @Test
    void recordsAMovementWhenConfirmingASale() throws Exception {
        Inventory inventory = stock(0, 4);

        service.confirmSale(PRODUCT, null, WAREHOUSE, 4, USER);

        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(captor.capture());
        assertEquals(MovementType.SALE_OUTBOUND, captor.getValue().getType());
        assertEquals(0, inventory.getReservedQuantity());
    }

    @Test
    void recordsAnInitialMovementWhenRegisteringInventory() throws Exception {
        when(catalogService.findProductById(PRODUCT)).thenReturn(new PhysicalProduct());
        when(inventoryRepository.findByProductVariantAndWarehouse(PRODUCT, null, WAREHOUSE))
                .thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(call -> {
            Inventory saved = call.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        Inventory result = service.register(PRODUCT, null, WAREHOUSE, 8, USER);

        assertEquals(8, result.getAvailableQuantity());
        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(captor.capture());
        assertEquals(MovementType.INBOUND, captor.getValue().getType());
        assertEquals(8, captor.getValue().getQuantity());
    }

    @Test
    void rejectsInventoryForADigitalProduct() throws Exception {
        when(catalogService.findProductById(PRODUCT)).thenReturn(new DigitalProduct());

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(PRODUCT, null, WAREHOUSE, 5, USER));

        assertTrue(error.getMessage().contains("does not manage inventory"));
        verify(inventoryRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateInventoryForTheSameProductAndWarehouse() throws Exception {
        when(catalogService.findProductById(PRODUCT)).thenReturn(new PhysicalProduct());
        when(inventoryRepository.findByProductVariantAndWarehouse(PRODUCT, null, WAREHOUSE))
                .thenReturn(Optional.of(new Inventory()));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(PRODUCT, null, WAREHOUSE, 5, USER));

        assertTrue(error.getMessage().contains("already has inventory"));
    }
}
