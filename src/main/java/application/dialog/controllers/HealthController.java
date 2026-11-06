package application.dialog.controllers;

import application.dialog.responses.HealthResponse;
import application.models.services.HealthService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "00 Salud", description = "Indica si el API responde y si alcanza la base de datos")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> check() {
        boolean databaseReachable = healthService.isDatabaseReachable();
        HealthResponse body = HealthResponse.of(databaseReachable);

        return databaseReachable
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
