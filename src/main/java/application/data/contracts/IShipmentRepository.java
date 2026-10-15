package application.data.contracts;

import application.data.enums.ShipmentStatus;
import application.data.entities.Shipment;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IShipmentRepository extends IRepository<Shipment> {
    Optional<Shipment> findByOrderId(Long orderId) throws SQLException;

    Optional<Shipment> findByTrackingNumber(String trackingNumber) throws SQLException;

    List<Shipment> findByStatus(ShipmentStatus status) throws SQLException;
}
