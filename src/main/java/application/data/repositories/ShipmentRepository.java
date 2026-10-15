package application.data.repositories;

import application.data.contracts.IShipmentRepository;
import application.data.entities.Shipment;
import application.data.enums.ShipmentStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ShipmentRepository extends BaseRepository<Shipment> implements IShipmentRepository {

    @Override
    protected String getTableName() {
        return "shipment";
    }

    @Override
    protected Shipment mapRow(ResultSet row) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setId(row.getLong("id"));
        shipment.setOrderId(toIdentifier(row, "order_id"));
        shipment.setSourceWarehouseId(toIdentifier(row, "source_warehouse_id"));
        shipment.setLogisticsOperatorId(toIdentifier(row, "logistics_operator_id"));
        shipment.setTrackingNumber(row.getString("tracking_number"));
        shipment.setCarrier(row.getString("carrier"));
        shipment.setDeliveryAddress(row.getString("delivery_address"));
        shipment.setStatus(ShipmentStatus.valueOf(row.getString("status")));
        shipment.setDispatchedAt(toDateTime(row.getTimestamp("dispatched_at")));
        shipment.setDeliveredAt(toDateTime(row.getTimestamp("delivered_at")));
        return shipment;
    }

    @Override
    public Shipment save(Shipment entity) throws SQLException {
        String sql = "INSERT INTO shipment "
                + "(order_id, source_warehouse_id, logistics_operator_id, tracking_number, carrier, "
                + "delivery_address, status, dispatched_at, delivered_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getOrderId(),
                entity.getSourceWarehouseId(),
                entity.getLogisticsOperatorId(),
                entity.getTrackingNumber(),
                entity.getCarrier(),
                entity.getDeliveryAddress(),
                entity.getStatus().name(),
                entity.getDispatchedAt(),
                entity.getDeliveredAt()));
        return entity;
    }

    @Override
    public boolean update(Shipment entity) throws SQLException {
        String sql = "UPDATE shipment SET logistics_operator_id = ?, carrier = ?, status = ?, "
                + "dispatched_at = ?, delivered_at = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getLogisticsOperatorId(),
                entity.getCarrier(),
                entity.getStatus().name(),
                entity.getDispatchedAt(),
                entity.getDeliveredAt(),
                entity.getId());
    }

    @Override
    public Optional<Shipment> findByOrderId(Long orderId) throws SQLException {
        return queryOne("SELECT * FROM shipment WHERE order_id = ?", orderId);
    }

    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) throws SQLException {
        return queryOne("SELECT * FROM shipment WHERE tracking_number = ?", trackingNumber);
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) throws SQLException {
        return queryList("SELECT * FROM shipment WHERE status = ? ORDER BY id DESC", status.name());
    }
}
