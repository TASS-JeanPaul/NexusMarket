package application.models.reports;

import application.data.views.CarrierPerformanceRow;

import java.math.BigDecimal;

public record CarrierQuality(CarrierPerformanceRow performance, BigDecimal failureRatePercent) {
}
