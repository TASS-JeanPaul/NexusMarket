package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.ProductReturn;
import application.data.entities.ProductReturnDetail;
import application.data.entities.User;
import application.data.enums.ReturnStatus;
import application.data.enums.Role;
import application.data.ports.out.IOrderDetailRepository;
import application.data.ports.out.IProductReturnDetailRepository;
import application.data.ports.out.IProductReturnRepository;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnServiceTest {

    private static final Long ORDER = 5L;
    private static final Long BUYER = 9L;
    private static final Long ORDER_LINE = 20L;

    @Mock
    private IProductReturnRepository returnRepository;
    @Mock
    private IProductReturnDetailRepository returnDetailRepository;
    @Mock
    private IOrderDetailRepository orderDetailRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private UserService userService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private CatalogService catalogService;

    private MockedStatic<TransactionManager> transactions;
    private ReturnService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new ReturnService(returnRepository, returnDetailRepository, orderDetailRepository,
                orderService, userService, inventoryService, catalogService);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    private void purchased(int quantity) throws Exception {
        Order order = new Order();
        order.setId(ORDER);
        order.setBuyerId(BUYER);
        when(orderService.requireReturnable(ORDER)).thenReturn(order);
        orderLine(quantity);
    }

    private void orderLine(int quantity) throws Exception {
        OrderDetail line = new OrderDetail();
        line.setId(ORDER_LINE);
        line.setOrderId(ORDER);
        line.setProductName("Keyboard");
        line.setQuantity(quantity);
        line.setUnitPrice(new BigDecimal("10.00"));
        when(orderDetailRepository.findById(ORDER_LINE)).thenReturn(Optional.of(line));
    }

    private ProductReturnDetail units(int quantity) {
        ProductReturnDetail detail = new ProductReturnDetail();
        detail.setOrderDetailId(ORDER_LINE);
        detail.setReturnedQuantity(quantity);
        return detail;
    }

    private ProductReturnDetail previous(Long returnId, int quantity, ReturnStatus parentStatus) throws Exception {
        ProductReturnDetail detail = units(quantity);
        detail.setReturnId(returnId);
        ProductReturn parent = new ProductReturn();
        parent.setId(returnId);
        parent.setStatus(parentStatus);
        when(returnRepository.findById(returnId)).thenReturn(Optional.of(parent));
        return detail;
    }

    @Test
    void rejectsReturningMoreUnitsThanPurchased() throws Exception {
        purchased(3);
        when(returnDetailRepository.findByOrderDetailId(ORDER_LINE)).thenReturn(List.of());

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(BUYER, ORDER, "broken", List.of(units(4))));

        assertTrue(error.getMessage().contains("Only 3 units of Keyboard can still be returned"));
        verify(returnRepository, never()).save(any());
    }

    @Test
    void rejectsReturningMoreThanPurchasedMinusAlreadyReturned() throws Exception {
        purchased(5);
        List<ProductReturnDetail> earlier = List.of(previous(41L, 2, ReturnStatus.APPROVED),
                previous(42L, 1, ReturnStatus.REQUESTED));
        when(returnDetailRepository.findByOrderDetailId(ORDER_LINE)).thenReturn(earlier);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(BUYER, ORDER, "broken", List.of(units(3))));

        assertTrue(error.getMessage().contains("Only 2 units"));
        verify(returnRepository, never()).save(any());
    }

    @Test
    void acceptsReturningExactlyTheRemainingUnits() throws Exception {
        purchased(5);
        List<ProductReturnDetail> earlier = List.of(previous(41L, 3, ReturnStatus.COMPLETED));
        when(returnDetailRepository.findByOrderDetailId(ORDER_LINE)).thenReturn(earlier);
        when(returnRepository.save(any(ProductReturn.class))).thenAnswer(call -> {
            ProductReturn saved = call.getArgument(0);
            saved.setId(40L);
            return saved;
        });

        ProductReturn result = service.request(BUYER, ORDER, "broken", List.of(units(2)));

        assertEquals(ReturnStatus.REQUESTED, result.getStatus());
        assertEquals(1, result.getDetails().size());
        verify(returnDetailRepository).save(any(ProductReturnDetail.class));
    }

    @Test
    void acceptsReturningUnitsOfAPreviouslyRejectedReturn() throws Exception {
        purchased(2);
        List<ProductReturnDetail> earlier = List.of(previous(41L, 2, ReturnStatus.REJECTED));
        when(returnDetailRepository.findByOrderDetailId(ORDER_LINE)).thenReturn(earlier);
        when(returnRepository.save(any(ProductReturn.class))).thenAnswer(call -> {
            ProductReturn saved = call.getArgument(0);
            saved.setId(40L);
            return saved;
        });

        ProductReturn result = service.request(BUYER, ORDER, "broken", List.of(units(2)));

        assertEquals(ReturnStatus.REQUESTED, result.getStatus());
        verify(returnDetailRepository).save(any(ProductReturnDetail.class));
    }

    @Test
    void rejectsReturningUnitsOfAnApprovedReturn() throws Exception {
        purchased(2);
        List<ProductReturnDetail> earlier = List.of(previous(41L, 2, ReturnStatus.APPROVED));
        when(returnDetailRepository.findByOrderDetailId(ORDER_LINE)).thenReturn(earlier);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(BUYER, ORDER, "broken", List.of(units(2))));

        assertTrue(error.getMessage().contains("can still be returned"));
        verify(returnRepository, never()).save(any());
    }

    @Test
    void rejectsReturningAnOrderOfAnotherBuyer() throws Exception {
        Order order = new Order();
        order.setId(ORDER);
        order.setBuyerId(99L);
        when(orderService.requireReturnable(ORDER)).thenReturn(order);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(BUYER, ORDER, "broken", List.of(units(1))));

        assertTrue(error.getMessage().contains("does not belong to the buyer"));
    }

    @Test
    void rejectsAReturnWithoutLines() throws Exception {
        Order order = new Order();
        order.setBuyerId(BUYER);
        when(orderService.requireReturnable(ORDER)).thenReturn(order);

        assertThrows(BusinessRuleException.class, () -> service.request(BUYER, ORDER, "broken", List.of()));
    }

    @Test
    void rejectsReturningZeroUnits() throws Exception {
        Order order = new Order();
        order.setBuyerId(BUYER);
        when(orderService.requireReturnable(ORDER)).thenReturn(order);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(BUYER, ORDER, "broken", List.of(units(0))));

        assertTrue(error.getMessage().contains("must be greater than zero"));
    }

    @Test
    void rejectsApprovingAReturnThatIsAlreadyCompleted() throws Exception {
        ProductReturn productReturn = new ProductReturn();
        productReturn.setStatus(ReturnStatus.COMPLETED);
        when(returnRepository.findById(40L)).thenReturn(Optional.of(productReturn));
        User administrator = new User();
        administrator.setRole(Role.ADMINISTRATOR);
        when(userService.findActiveOrFail(1L)).thenReturn(administrator);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.approve(40L, 1L));

        assertTrue(error.getMessage().contains("cannot move from COMPLETED to APPROVED"));
    }

    @Test
    void computesTheReturnedValueFromTheOrderLinePrices() throws Exception {
        orderLine(5);
        when(returnDetailRepository.findByReturnId(40L)).thenReturn(List.of(units(3)));

        assertEquals(0, new BigDecimal("30.00").compareTo(service.returnedValue(40L)));
    }
}
