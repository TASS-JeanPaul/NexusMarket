package application.dialog.controllers;

import application.data.entities.User;
import application.dialog.requests.LoginRequest;
import application.dialog.responses.TokenResponse;
import application.dialog.security.JwtService;
import application.models.ports.in.IUserUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "00 Autenticacion", description = "Obtencion del token que exige la restriccion RG-01")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String TOKEN_TYPE = "Bearer";

    private final IUserUseCase userUseCase;
    private final JwtService jwtService;

    public AuthController(IUserUseCase userUseCase, JwtService jwtService) {
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userUseCase.authenticate(request.email(), request.password());
        String token = jwtService.generate(user);

        return new TokenResponse(
                token,
                TOKEN_TYPE,
                user.getEmail(),
                user.getRole().name(),
                jwtService.expirationOf(token));
    }
}
