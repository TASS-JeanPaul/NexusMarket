package application.dialog.security;

import application.data.entities.User;
import application.models.ports.in.IUserUseCase;
import application.shared.exceptions.AuthenticationException;
import application.shared.exceptions.AuthorizationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private final IUserUseCase userUseCase;

    public CurrentUser(IUserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AuthenticationException("This operation requires an authenticated user");
        }
        return userUseCase.findByEmail(authentication.getName());
    }

    public Long id() {
        return get().getId();
    }

    public void requireSelf(Long claimedId) {
        if (!id().equals(claimedId)) {
            throw new AuthorizationException(
                    "A participant cannot operate on information that belongs to another user");
        }
    }
}
