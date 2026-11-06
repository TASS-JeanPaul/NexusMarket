package application.data.views;

public record CarrierPerformanceRow(
        String carrier,
        long shipments,
        long delivered,
        long failed) {
}
