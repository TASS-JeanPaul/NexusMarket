package application.models.ports.in;

import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.data.ports.out.IUserRepository;
import application.data.entities.User;
import java.time.LocalDateTime;
import java.util.List;

public interface IUserUseCase {

    User register(User user);

    User findById(Long id);

    List<User> findAll();

    List<User> findByRole(Role role);

    User findActiveOrFail(Long id);

    User requireRole(Long id, Role expectedRole);

    boolean isEmailAvailable(String email);

    User updateContactInformation(Long id, String email, String phone);

    User update(User user);

    User changeStatus(Long id, UserStatus status);

    User block(Long id);

    User activate(Long id);

    User deactivate(Long id);
}
