package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.ShipmentStatus;

@Getter
@Setter
public class Shipment {
    private Long id;
    private Long orderId;
    private Long sourceWarehouseId;
    private Long logisticsOperatorId;
    private String trackingNumber;
    private String carrier;
    private String deliveryAddress;
    private ShipmentStatus status;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
}
