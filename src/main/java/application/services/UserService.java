package application.services;

import application.domain.enums.Role;
import application.domain.enums.UserStatus;
import application.domain.interfaces.IUserRepository;
import application.domain.models.User;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService extends BaseService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return execute(() -> {
            validateCommonData(user);
            require(user.getRole() != null, "The role is required");
            require(userRepository.findByEmail(user.getEmail()).isEmpty(),
                    "The email " + user.getEmail() + " is already registered");
            require(userRepository.findByIdentityDocument(user.getIdentityDocument()).isEmpty(),
                    "The identity document " + user.getIdentityDocument() + " is already registered");

            user.setStatus(UserStatus.ACTIVE);
            user.setRegisteredAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    public User findById(Long id) {
        requireIdentifier(id, "The user identifier");
        return execute(() -> findOrFail(userRepository.findById(id), "User", id));
    }

    public List<User> findAll() {
        return execute(userRepository::findAll);
    }

    public List<User> findByRole(Role role) {
        require(role != null, "The role is required");
        return execute(() -> userRepository.findByRole(role));
    }

    public User findActiveOrFail(Long id) {
        User user = findById(id);
        require(user.getStatus() == UserStatus.ACTIVE,
                "The user " + id + " is not active and cannot operate in the system");
        return user;
    }

    public User requireRole(Long id, Role expectedRole) {
        User user = findActiveOrFail(id);
        require(user.getRole() == expectedRole,
                "The user " + id + " does not have the required role " + expectedRole);
        return user;
    }

    public boolean isEmailAvailable(String email) {
        requireText(email, "The email");
        return execute(() -> userRepository.findByEmail(email).isEmpty());
    }

    public User updateContactInformation(Long id, String email, String phone) {
        return execute(() -> {
            User user = findById(id);
            requireText(email, "The email");
            require(userRepository.findByEmail(email)
                            .filter(other -> !other.getId().equals(id))
                            .isEmpty(),
                    "The email " + email + " belongs to another user");

            user.setEmail(email);
            user.setPhone(phone);
            userRepository.update(user);
            return user;
        });
    }

    public User update(User user) {
        return execute(() -> {
            requireIdentifier(user.getId(), "The user identifier");
            User stored = findById(user.getId());
            validateCommonData(user);
            require(stored.getRole() == user.getRole(),
                    "The role of a user cannot be changed once it has been assigned");

            userRepository.update(user);
            return user;
        });
    }

    public User changeStatus(Long id, UserStatus status) {
        return execute(() -> {
            require(status != null, "The status is required");
            User user = findById(id);
            user.setStatus(status);
            userRepository.update(user);
            return user;
        });
    }

    public User block(Long id) {
        return changeStatus(id, UserStatus.BLOCKED);
    }

    public User activate(Long id) {
        return changeStatus(id, UserStatus.ACTIVE);
    }

    public User deactivate(Long id) {
        return changeStatus(id, UserStatus.INACTIVE);
    }

    private void validateCommonData(User user) {
        require(user != null, "The user is required");
        requireText(user.getFullName(), "The full name");
        requireText(user.getIdentityDocument(), "The identity document");
        requireText(user.getEmail(), "The email");
        require(user.getEmail().contains("@"), "The email format is not valid");
    }
}
