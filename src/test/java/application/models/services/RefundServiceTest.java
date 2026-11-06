package application.models.services;

import application.data.config.TransactionManager;
import application.data.entities.Refund;
import application.data.enums.RefundMethod;
import application.data.enums.RefundStatus;
import application.data.enums.Role;
import application.data.ports.out.IRefundRepository;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
class RefundServiceTest {

    private static final Long RETURN = 4L;

    @Mock
    private IRefundRepository refundRepository;
    @Mock
    private ReturnService returnService;
    @Mock
    private UserService userService;

    private MockedStatic<TransactionManager> transactions;
    private RefundService service;

    @BeforeEach
    void setUp() {
        transactions = TransactionBypass.install();
        service = new RefundService(refundRepository, returnService, userService);
    }

    @AfterEach
    void tearDown() {
        transactions.close();
    }

    @Test
    void rejectsARefundAboveTheReturnedValue() throws Exception {
        when(refundRepository.findByReturnId(RETURN)).thenReturn(Optional.empty());
        when(returnService.returnedValue(RETURN)).thenReturn(new BigDecimal("30.00"));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(RETURN, new BigDecimal("30.01"), RefundMethod.CARD));

        assertTrue(error.getMessage().contains("cannot exceed the value of the returned products"));
        verify(refundRepository, never()).save(any());
    }

    @Test
    void acceptsARefundEqualToTheReturnedValue() throws Exception {
        when(refundRepository.findByReturnId(RETURN)).thenReturn(Optional.empty());
        when(returnService.returnedValue(RETURN)).thenReturn(new BigDecimal("30.00"));
        when(refundRepository.save(any(Refund.class))).thenAnswer(call -> call.getArgument(0));

        Refund refund = service.request(RETURN, new BigDecimal("30.00"), RefundMethod.CARD);

        assertEquals(RefundStatus.PENDING, refund.getStatus());
        assertEquals(RETURN, refund.getReturnId());
    }

    @Test
    void rejectsASecondRefundForTheSameReturn() throws Exception {
        when(refundRepository.findByReturnId(RETURN)).thenReturn(Optional.of(new Refund()));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.request(RETURN, new BigDecimal("5"), RefundMethod.CARD));

        assertTrue(error.getMessage().contains("already has a refund"));
    }

    @Test
    void rejectsAZeroRefundAmount() throws Exception {
        assertThrows(BusinessRuleException.class,
                () -> service.request(RETURN, BigDecimal.ZERO, RefundMethod.CARD));
        verify(refundRepository, never()).save(any());
    }

    @Test
    void rejectsPayingARefundThatWasNeverApproved() throws Exception {
        Refund refund = new Refund();
        refund.setStatus(RefundStatus.PENDING);
        when(refundRepository.findById(8L)).thenReturn(Optional.of(refund));

        BusinessRuleException error = assertThrows(BusinessRuleException.class, () -> service.markAsPaid(8L));

        assertTrue(error.getMessage().contains("cannot move from PENDING to PAID"));
    }

    @Test
    void approvesAPendingRefundRequiringAnAdministrator() throws Exception {
        Refund refund = new Refund();
        refund.setStatus(RefundStatus.PENDING);
        when(refundRepository.findById(8L)).thenReturn(Optional.of(refund));

        Refund result = service.approve(8L, 1L);

        verify(userService).requireRole(1L, Role.ADMINISTRATOR);
        assertEquals(RefundStatus.APPROVED, result.getStatus());
        assertEquals(1L, result.getAuthorizedById());
    }
}
