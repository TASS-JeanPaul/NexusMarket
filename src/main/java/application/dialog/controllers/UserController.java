package application.dialog.controllers;

import application.data.entities.User;
import application.data.enums.Role;
import application.data.enums.UserStatus;
import application.dialog.requests.ContactInformationRequest;
import application.models.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping
    public List<User> findAll(@RequestParam(required = false) Role role) {
        return role == null ? userService.findAll() : userService.findByRole(role);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}/contact-information")
    public User updateContactInformation(@PathVariable Long id,
                                         @RequestBody ContactInformationRequest request) {
        return userService.updateContactInformation(id, request.email(), request.phone());
    }

    @PutMapping("/{id}/status")
    public User changeStatus(@PathVariable Long id, @RequestParam UserStatus value) {
        return userService.changeStatus(id, value);
    }
}
