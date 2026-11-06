package application.models.services;

import application.data.entities.User;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.data.ports.out.IUserRepository;
import application.models.ports.out.IPasswordHasher;
import application.shared.exceptions.AuthenticationException;
import application.shared.exceptions.BusinessRuleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IPasswordHasher passwordHasher;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, passwordHasher);
    }

    private User user(Role role) {
        User user = new User();
        user.setId(1L);
        user.setFullName("Luis Mora");
        user.setIdentityDocument("555");
        user.setEmail("luis@mail.com");
        user.setPassword("secret");
        user.setRole(role);
        return user;
    }

    @Test
    void rejectsRegisteringADuplicatedEmail() throws Exception {
        when(userRepository.findByEmail("luis@mail.com")).thenReturn(Optional.of(new User()));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.register(user(Role.SUPERVISOR)));

        assertTrue(error.getMessage().contains("already registered"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectsRegisteringADuplicatedIdentityDocument() throws Exception {
        when(userRepository.findByEmail("luis@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByIdentityDocument("555")).thenReturn(Optional.of(new User()));

        assertThrows(BusinessRuleException.class, () -> service.register(user(Role.SUPERVISOR)));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registersAnActiveUserWithAHashedPassword() throws Exception {
        when(userRepository.findByEmail("luis@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByIdentityDocument("555")).thenReturn(Optional.empty());
        when(passwordHasher.hash("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User result = service.register(user(Role.SUPERVISOR));

        assertEquals("hashed", result.getPassword());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    void rejectsRegisteringAnInvalidEmail() {
        User invalid = user(Role.SUPERVISOR);
        invalid.setEmail("not-an-email");

        BusinessRuleException error = assertThrows(BusinessRuleException.class, () -> service.register(invalid));

        assertTrue(error.getMessage().contains("email format"));
    }

    @Test
    void rejectsChangingTheRoleOnUpdate() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(Role.SUPERVISOR)));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.update(user(Role.ADMINISTRATOR)));

        assertTrue(error.getMessage().contains("role of a user cannot be changed"));
        verify(userRepository, never()).update(any());
    }

    @Test
    void updatesAUserKeepingTheSameRole() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(Role.SUPERVISOR)));
        User changes = user(Role.SUPERVISOR);
        changes.setFullName("Luis Mora Jr");

        User result = service.update(changes);

        assertEquals("Luis Mora Jr", result.getFullName());
        verify(userRepository).update(changes);
    }

    @Test
    void rejectsContactEmailBelongingToAnotherUser() throws Exception {
        User other = user(Role.SUPERVISOR);
        other.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(Role.SUPERVISOR)));
        when(userRepository.findByEmail("taken@mail.com")).thenReturn(Optional.of(other));

        assertThrows(BusinessRuleException.class,
                () -> service.updateContactInformation(1L, "taken@mail.com", "1"));
    }

    @Test
    void rejectsOperatingWithAnInactiveUser() throws Exception {
        User blocked = user(Role.ADMINISTRATOR);
        blocked.setStatus(UserStatus.BLOCKED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(blocked));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.requireRole(1L, Role.ADMINISTRATOR));

        assertTrue(error.getMessage().contains("not active"));
    }

    @Test
    void rejectsAUserWithoutTheRequiredRole() throws Exception {
        User seller = user(Role.SELLER);
        seller.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));

        BusinessRuleException error = assertThrows(BusinessRuleException.class,
                () -> service.requireRole(1L, Role.ADMINISTRATOR));

        assertTrue(error.getMessage().contains("required role ADMINISTRATOR"));
    }

    @Test
    void rejectsAuthenticationWithAWrongPassword() throws Exception {
        User stored = user(Role.BUYER);
        stored.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByEmail("luis@mail.com")).thenReturn(Optional.of(stored));
        when(passwordHasher.matches("bad", "secret")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> service.authenticate("luis@mail.com", "bad"));
    }
}
