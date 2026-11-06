package application.models.reports;

import application.data.views.CarrierPerformanceRow;
import application.data.views.ProductReturnsRow;

import java.util.List;

public record AfterSalesReport(List<ProductReturnsRow> returnsByProduct, List<CarrierQuality> carriers) {
}
