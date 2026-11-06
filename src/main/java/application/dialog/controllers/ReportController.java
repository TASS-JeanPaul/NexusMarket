package application.dialog.controllers;

import application.data.views.OrderStatusRow;
import application.models.reports.AfterSalesReport;
import application.models.reports.CategoryShare;
import application.models.reports.ProductPerformance;
import application.models.reports.SellerPerformance;
import application.models.reports.StockStatus;
import application.models.ports.in.IReportUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "13 Reportes", description = "Informacion administrativa consolidada para consulta (OBJ-12)")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final int DEFAULT_WINDOW_DAYS = 90;

    private final IReportUseCase reportUseCase;

    public ReportController(IReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GetMapping("/sales-by-seller")
    public List<SellerPerformance> salesBySeller(
            @RequestParam(defaultValue = "" + DEFAULT_WINDOW_DAYS) int days) {
        return reportUseCase.sellerPerformance(days);
    }

    @GetMapping("/sales-by-category")
    public List<CategoryShare> salesByCategory(
            @RequestParam(defaultValue = "" + DEFAULT_WINDOW_DAYS) int days) {
        return reportUseCase.categoryShare(days);
    }

    @GetMapping("/sales-by-product")
    public List<ProductPerformance> salesByProduct(
            @RequestParam(defaultValue = "" + DEFAULT_WINDOW_DAYS) int days) {
        return reportUseCase.productPerformance(days);
    }

    @GetMapping("/orders-by-status")
    public List<OrderStatusRow> ordersByStatus(
            @RequestParam(defaultValue = "" + DEFAULT_WINDOW_DAYS) int days) {
        return reportUseCase.ordersByStatus(days);
    }

    @GetMapping("/inventory")
    public List<StockStatus> inventory(@RequestParam(required = false) Long warehouseId) {
        return reportUseCase.stock(warehouseId);
    }

    @GetMapping("/after-sales")
    public AfterSalesReport afterSales(
            @RequestParam(defaultValue = "" + DEFAULT_WINDOW_DAYS) int days) {
        return reportUseCase.afterSales(days);
    }
}
