package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.Seller;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.data.ports.out.ISellerRepository;
import application.models.ports.out.IPasswordHasher;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    private static final Long ADMIN = 1L;

    @Mock
    private ISellerRepository sellerRepository;
    @Mock
    private UserService userService;
    @Mock
    private IPasswordHasher passwordHasher;

    private MockedStatic<TransactionManager> transactions;
    private SellerService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new SellerService(sellerRepository, userService, passwordHasher);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    private Seller seller() {
        Seller seller = new Seller();
        seller.setFullName("Ana Perez");
        seller.setIdentityDocument("123");
        seller.setEmail("ana@shop.com");
        seller.setBusinessName("Ana Shop");
        seller.setTaxId("TAX-1");
        seller.setPassword("secret");
        return seller;
    }

    @Test
    void rejectsRegistrationWhenTheRegistrantIsNotAnAdministrator() throws Exception {
        doThrow(new BusinessRuleException("The user 1 does not have the required role ADMINISTRATOR"))
                .when(userService).requireRole(ADMIN, Role.ADMINISTRATOR);

        assertThrows(BusinessRuleException.class, () -> service.register(seller(), ADMIN));

        verify(sellerRepository, never()).save(any());
    }

    @Test
    void rejectsRegistrationWithoutAnAdministratorIdentifier() throws Exception {
        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(seller(), null));

        assertTrue(error.getMessage().contains("sellers cannot register themselves"));
        verify(sellerRepository, never()).save(any());
    }

    @Test
    void rejectsRegistrationWithADuplicatedTaxId() throws Exception {
        when(userService.isEmailAvailable("ana@shop.com")).thenReturn(true);
        when(sellerRepository.findByTaxId("TAX-1")).thenReturn(Optional.of(new Seller()));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(seller(), ADMIN));

        assertTrue(error.getMessage().contains("already registered"));
    }

    @Test
    void registersAnActiveSellerHashingThePassword() throws Exception {
        when(userService.isEmailAvailable("ana@shop.com")).thenReturn(true);
        when(sellerRepository.findByTaxId("TAX-1")).thenReturn(Optional.empty());
        when(passwordHasher.hash("secret")).thenReturn("hashed");
        when(sellerRepository.save(any(Seller.class))).thenAnswer(call -> call.getArgument(0));

        Seller result = service.register(seller(), ADMIN);

        verify(userService).requireRole(ADMIN, Role.ADMINISTRATOR);
        assertEquals("hashed", result.getPassword());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
        assertEquals(ADMIN, result.getRegisteredById());
        assertEquals(Role.SELLER, result.getRole());
    }
}
