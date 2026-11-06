package application.models.usecases;

import application.models.ports.in.IUserUseCase;
import application.models.services.UserService;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.data.ports.out.IUserRepository;
import application.data.entities.User;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserUseCase implements IUserUseCase {

    private final UserService userService;

    public UserUseCase(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User register(User user) {
        return userService.register(user);
    }

    @Override
    public User findById(Long id) {
        return userService.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    @Override
    public List<User> findByRole(Role role) {
        return userService.findByRole(role);
    }

    @Override
    public User findActiveOrFail(Long id) {
        return userService.findActiveOrFail(id);
    }

    @Override
    public User requireRole(Long id, Role expectedRole) {
        return userService.requireRole(id, expectedRole);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return userService.isEmailAvailable(email);
    }

    @Override
    public User updateContactInformation(Long id, String email, String phone) {
        return userService.updateContactInformation(id, email, phone);
    }

    @Override
    public User update(User user) {
        return userService.update(user);
    }

    @Override
    public User changeStatus(Long id, UserStatus status) {
        return userService.changeStatus(id, status);
    }

    @Override
    public User block(Long id) {
        return userService.block(id);
    }

    @Override
    public User activate(Long id) {
        return userService.activate(id);
    }

    @Override
    public User deactivate(Long id) {
        return userService.deactivate(id);
    }
}
