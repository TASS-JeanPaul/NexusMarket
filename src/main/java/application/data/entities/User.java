package application.data.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import application.data.enums.UserStatus;
import application.data.enums.Role;

@Getter
@Setter
public class User {
    private Long id;
    private String fullName;
    private String identityDocument;
    private String email;
    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Role role;
    private UserStatus status;
    private LocalDateTime registeredAt;
}
