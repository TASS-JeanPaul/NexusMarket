package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.Address;
import application.data.entities.Buyer;
import application.data.enums.CommercialStatus;
import application.data.enums.UserStatus;
import application.data.ports.out.IAddressRepository;
import application.data.ports.out.IBuyerRepository;
import application.models.ports.out.IPasswordHasher;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerServiceTest {

    private static final Long BUYER = 3L;

    @Mock
    private IBuyerRepository buyerRepository;
    @Mock
    private IAddressRepository addressRepository;
    @Mock
    private UserService userService;
    @Mock
    private IPasswordHasher passwordHasher;

    private MockedStatic<TransactionManager> transactions;
    private BuyerService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new BuyerService(buyerRepository, addressRepository, userService, passwordHasher);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    private Buyer storedBuyer(UserStatus status, CommercialStatus commercial) throws Exception {
        Buyer buyer = new Buyer();
        buyer.setId(BUYER);
        buyer.setStatus(status);
        buyer.setCommercialStatus(commercial);
        when(buyerRepository.findById(BUYER)).thenReturn(Optional.of(buyer));
        return buyer;
    }

    private Buyer newBuyer() {
        Buyer buyer = new Buyer();
        buyer.setFullName("Eva");
        buyer.setIdentityDocument("1");
        buyer.setEmail("eva@mail.com");
        buyer.setPassword("pw");
        return buyer;
    }

    private Address address(boolean primary) {
        Address address = new Address();
        address.setStreet("Main 1");
        address.setCity("Quito");
        address.setPrimary(primary);
        return address;
    }

    @Test
    void rejectsPurchasingWhenTheBuyerIsSuspended() throws Exception {
        storedBuyer(UserStatus.ACTIVE, CommercialStatus.SUSPENDED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.requirePurchaseEnabled(BUYER));

        assertTrue(error.getMessage().contains("commercially suspended"));
    }

    @Test
    void rejectsPurchasingWhenTheBuyerIsNotActive() throws Exception {
        storedBuyer(UserStatus.BLOCKED, CommercialStatus.ENABLED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.requirePurchaseEnabled(BUYER));

        assertTrue(error.getMessage().contains("is not active"));
    }

    @Test
    void allowsPurchasingWhenTheBuyerIsActiveAndEnabled() throws Exception {
        Buyer buyer = storedBuyer(UserStatus.ACTIVE, CommercialStatus.ENABLED);

        assertEquals(buyer, service.requirePurchaseEnabled(BUYER));
    }

    @Test
    void suspendsAndEnablesTheCommercialStatus() throws Exception {
        Buyer buyer = storedBuyer(UserStatus.ACTIVE, CommercialStatus.ENABLED);

        assertEquals(CommercialStatus.SUSPENDED, service.suspend(BUYER).getCommercialStatus());
        assertEquals(CommercialStatus.ENABLED, service.enable(BUYER).getCommercialStatus());
        verify(buyerRepository, times(2)).update(buyer);
    }

    @Test
    void makesTheFirstAddressThePrimaryOne() throws Exception {
        storedBuyer(UserStatus.ACTIVE, CommercialStatus.ENABLED);
        when(addressRepository.findByBuyerId(BUYER)).thenReturn(List.of());
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address saved = service.addAddress(BUYER, address(false));

        assertTrue(saved.isPrimary());
        assertEquals(BUYER, saved.getBuyerId());
    }

    @Test
    void keepsTheExistingPrimaryWhenAddingASecondAddress() throws Exception {
        storedBuyer(UserStatus.ACTIVE, CommercialStatus.ENABLED);
        Address existing = address(true);
        when(addressRepository.findByBuyerId(BUYER)).thenReturn(List.of(existing));
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address saved = service.addAddress(BUYER, address(false));

        assertFalse(saved.isPrimary());
        assertTrue(existing.isPrimary());
        verify(addressRepository, never()).update(any());
    }

    @Test
    void movesThePrimaryFlagWhenANewPrimaryAddressIsAdded() throws Exception {
        storedBuyer(UserStatus.ACTIVE, CommercialStatus.ENABLED);
        Address existing = address(true);
        when(addressRepository.findByBuyerId(BUYER)).thenReturn(List.of(existing));
        when(addressRepository.save(any(Address.class))).thenAnswer(call -> call.getArgument(0));

        Address saved = service.addAddress(BUYER, address(true));

        assertTrue(saved.isPrimary());
        assertFalse(existing.isPrimary());
        verify(addressRepository).update(existing);
    }

    @Test
    void rejectsRemovingThePrimaryAddress() throws Exception {
        Address primary = address(true);
        primary.setBuyerId(BUYER);
        when(addressRepository.findById(11L)).thenReturn(Optional.of(primary));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.removeAddress(BUYER, 11L));

        assertTrue(error.getMessage().contains("primary address cannot be removed"));
        verify(addressRepository, never()).delete(any());
    }

    @Test
    void rejectsUsingAnAddressOfAnotherBuyer() throws Exception {
        Address foreign = address(false);
        foreign.setBuyerId(99L);
        when(addressRepository.findById(11L)).thenReturn(Optional.of(foreign));

        assertThrows(BusinessRuleException.class, () -> service.requireDeliveryAddress(BUYER, 11L));
    }

    @Test
    void rejectsRegisteringABuyerWithATakenEmail() throws Exception {
        when(userService.isEmailAvailable("eva@mail.com")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.register(newBuyer()));
        verify(buyerRepository, never()).save(any());
    }

    @Test
    void registersABuyerAsActiveAndEnabled() throws Exception {
        when(userService.isEmailAvailable("eva@mail.com")).thenReturn(true);
        when(passwordHasher.hash("pw")).thenReturn("hashed");
        when(buyerRepository.save(any(Buyer.class))).thenAnswer(call -> call.getArgument(0));

        Buyer result = service.register(newBuyer());

        assertEquals(UserStatus.ACTIVE, result.getStatus());
        assertEquals(CommercialStatus.ENABLED, result.getCommercialStatus());
        assertEquals("hashed", result.getPassword());
    }
}
