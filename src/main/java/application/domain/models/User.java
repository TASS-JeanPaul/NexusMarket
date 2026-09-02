package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.domain.enums.UserStatus;
import application.domain.enums.Role;

@Getter
@Setter
public class User {
    private Long id;
    private String fullName;
    private String identityDocument;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
    private LocalDateTime registeredAt;
}
