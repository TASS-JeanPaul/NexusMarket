package application.models.services;

import application.data.config.DatabaseConnection;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public boolean isDatabaseReachable() {
        return DatabaseConnection.getInstance().isReachable();
    }
}
