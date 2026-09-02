package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.domain.enums.OrderStatus;

@Getter
@Setter
public class Order {
    private Long id;
    private String orderNumber;
    private Long buyerId;
    private Long deliveryAddressId;
    private List<OrderDetail> details = new ArrayList<>();
    private OrderStatus status;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
