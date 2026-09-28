package application.domain.interfaces;

import application.domain.enums.Role;
import application.domain.models.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IUserRepository extends IRepository<User> {
    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findByIdentityDocument(String identityDocument) throws SQLException;

    List<User> findByRole(Role role) throws SQLException;
}
