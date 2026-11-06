package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.Order;
import application.data.enums.OrderStatus;
import application.data.ports.out.IOrderDetailRepository;
import application.data.ports.out.IOrderRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private IOrderRepository orderRepository;
    @Mock
    private IOrderDetailRepository orderDetailRepository;
    @Mock
    private CartService cartService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private CatalogService catalogService;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private BuyerService buyerService;

    private MockedStatic<TransactionManager> transactions;
    private OrderService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new OrderService(orderRepository, orderDetailRepository, cartService, inventoryService,
                catalogService, invoiceService, buyerService);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    private Order orderWith(OrderStatus status) throws Exception {
        Order order = new Order();
        order.setId(7L);
        order.setStatus(status);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        return order;
    }

    @Test
    void rejectsMovingACompletedOrder() throws Exception {
        orderWith(OrderStatus.COMPLETED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.changeStatus(7L, OrderStatus.SHIPPED));

        assertTrue(error.getMessage().contains("finalized"));
        verify(orderRepository, never()).update(any());
    }

    @Test
    void rejectsMovingACancelledOrder() throws Exception {
        orderWith(OrderStatus.CANCELLED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.changeStatus(7L, OrderStatus.PAID));

        assertTrue(error.getMessage().contains("finalized"));
        verify(orderRepository, never()).update(any());
    }

    @Test
    void rejectsCompletingACancelledOrder() throws Exception {
        orderWith(OrderStatus.CANCELLED);

        BusinessRuleException error = assertThrows(BusinessRuleException.class, () -> service.complete(7L));

        assertTrue(error.getMessage().contains("finalized"));
    }

    @Test
    void rejectsCancellingACompletedOrder() throws Exception {
        orderWith(OrderStatus.COMPLETED);
        when(orderDetailRepository.findByOrderId(7L)).thenReturn(List.of());

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.cancel(7L, 1L, 1L));

        assertTrue(error.getMessage().contains("finalized"));
        verify(orderRepository, never()).update(any());
    }

    @Test
    void rejectsAnInvalidTransition() throws Exception {
        orderWith(OrderStatus.PENDING_PAYMENT);

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.changeStatus(7L, OrderStatus.DELIVERED));

        assertTrue(error.getMessage().contains("cannot move from PENDING_PAYMENT to DELIVERED"));
    }

    @Test
    void appliesAValidTransition() throws Exception {
        Order order = orderWith(OrderStatus.PAID);

        Order result = service.markAsShipped(7L);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());
        verify(orderRepository).update(order);
    }

    @Test
    void completesADeliveredOrder() throws Exception {
        Order order = orderWith(OrderStatus.DELIVERED);

        Order result = service.complete(7L);

        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        assertNotNull(order.getCompletedAt());
        verify(orderRepository).update(order);
    }

    @Test
    void rejectsReturningAnOrderThatIsNotDelivered() throws Exception {
        orderWith(OrderStatus.PAID);

        assertThrows(BusinessRuleException.class, () -> service.requireReturnable(7L));
    }
}
