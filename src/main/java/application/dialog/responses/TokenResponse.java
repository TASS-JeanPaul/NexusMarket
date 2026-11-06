package application.dialog.responses;

import java.time.LocalDateTime;

public record TokenResponse(String token, String tokenType, String email, String role, LocalDateTime expiresAt) {
}
