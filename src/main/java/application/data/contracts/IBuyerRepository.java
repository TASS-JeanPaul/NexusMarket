package application.data.contracts;

import application.data.entities.Buyer;

import java.sql.SQLException;
import java.util.Optional;

public interface IBuyerRepository extends IRepository<Buyer> {
    Optional<Buyer> findByEmail(String email) throws SQLException;
}
