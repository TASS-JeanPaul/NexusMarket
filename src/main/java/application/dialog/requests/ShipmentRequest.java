package application.dialog.requests;

public record ShipmentRequest(
        Long orderId,
        Long sourceWarehouseId,
        Long logisticsOperatorId,
        String carrier,
        String deliveryAddress) {
}
