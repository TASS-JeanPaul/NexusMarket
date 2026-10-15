package application.data.repositories;

import application.data.contracts.IUserRepository;
import application.data.entities.User;
import application.data.enums.Role;
import application.data.enums.UserStatus;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> implements IUserRepository {

    @Override
    protected String getTableName() {
        return "app_user";
    }

    @Override
    protected User mapRow(ResultSet row) throws SQLException {
        User user = new User();
        user.setId(row.getLong("id"));
        user.setFullName(row.getString("full_name"));
        user.setIdentityDocument(row.getString("identity_document"));
        user.setEmail(row.getString("email"));
        user.setPhone(row.getString("phone"));
        user.setRole(Role.valueOf(row.getString("role")));
        user.setStatus(UserStatus.valueOf(row.getString("status")));
        user.setRegisteredAt(toDateTime(row.getTimestamp("registered_at")));
        return user;
    }

    @Override
    public User save(User entity) throws SQLException {
        String sql = "INSERT INTO app_user "
                + "(full_name, identity_document, email, phone, role, status, registered_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        entity.setId(insertAndGetId(sql,
                entity.getFullName(),
                entity.getIdentityDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRole().name(),
                entity.getStatus().name(),
                entity.getRegisteredAt()));
        return entity;
    }

    @Override
    public boolean update(User entity) throws SQLException {
        String sql = "UPDATE app_user SET full_name = ?, identity_document = ?, email = ?, phone = ?, "
                + "status = ? WHERE id = ?";

        return executeUpdate(sql,
                entity.getFullName(),
                entity.getIdentityDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus().name(),
                entity.getId());
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        return queryOne("SELECT * FROM app_user WHERE email = ?", email);
    }

    @Override
    public Optional<User> findByIdentityDocument(String identityDocument) throws SQLException {
        return queryOne("SELECT * FROM app_user WHERE identity_document = ?", identityDocument);
    }

    @Override
    public List<User> findByRole(Role role) throws SQLException {
        return queryList("SELECT * FROM app_user WHERE role = ? ORDER BY full_name", role.name());
    }
}
