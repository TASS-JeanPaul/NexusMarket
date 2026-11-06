package application.dialog.controllers;

import application.data.entities.User;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.dialog.requests.ContactInformationRequest;
import application.models.ports.in.IUserUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "01 Usuarios", description = "Identificacion y estado operativo de los usuarios")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserUseCase userUseCase;

    public UserController(IUserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody User user) {
        return userUseCase.register(user);
    }

    @GetMapping
    public List<User> findAll(@RequestParam(required = false) Role role) {
        return role == null ? userUseCase.findAll() : userUseCase.findByRole(role);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userUseCase.findById(id);
    }

    @PutMapping("/{id}/contact-information")
    public User updateContactInformation(@PathVariable Long id,
                                         @Valid @RequestBody ContactInformationRequest request) {
        return userUseCase.updateContactInformation(id, request.email(), request.phone());
    }

    @PutMapping("/{id}/status")
    public User changeStatus(@PathVariable Long id, @RequestParam UserStatus value) {
        return userUseCase.changeStatus(id, value);
    }
}
