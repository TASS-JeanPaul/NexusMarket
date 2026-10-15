package application.data.contracts;

import application.data.entities.Seller;

import java.sql.SQLException;
import java.util.Optional;

public interface ISellerRepository extends IRepository<Seller> {
    Optional<Seller> findByTaxId(String taxId) throws SQLException;
}
