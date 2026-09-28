package application.domain.interfaces;

import application.domain.models.Address;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IAddressRepository extends IRepository<Address> {
    List<Address> findByBuyerId(Long buyerId) throws SQLException;

    Optional<Address> findPrimaryByBuyerId(Long buyerId) throws SQLException;
}
