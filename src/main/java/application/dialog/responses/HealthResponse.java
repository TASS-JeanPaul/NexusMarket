package application.dialog.responses;

import java.time.LocalDateTime;

public record HealthResponse(String api, String database, LocalDateTime checkedAt) {

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    public static HealthResponse of(boolean databaseReachable) {
        return new HealthResponse(UP, databaseReachable ? UP : DOWN, LocalDateTime.now());
    }
}
