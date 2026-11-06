package application.models.usecases;

import application.models.ports.in.IReportUseCase;
import application.models.services.ReportService;
import application.data.ports.out.IReportRepository;
import application.data.views.CarrierPerformanceRow;
import application.data.views.CategorySalesRow;
import application.data.views.OrderStatusRow;
import application.data.views.ProductSalesRow;
import application.data.views.SellerSalesRow;
import application.data.views.WarehouseStockRow;
import application.models.reports.AfterSalesReport;
import application.models.reports.CarrierQuality;
import application.models.reports.CategoryShare;
import application.models.reports.ProductPerformance;
import application.models.reports.SellerPerformance;
import application.models.reports.StockStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ReportUseCase implements IReportUseCase {

    private final ReportService reportService;

    public ReportUseCase(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public List<SellerPerformance> sellerPerformance(int days) {
        return reportService.sellerPerformance(days);
    }

    @Override
    public List<CategoryShare> categoryShare(int days) {
        return reportService.categoryShare(days);
    }

    @Override
    public List<ProductPerformance> productPerformance(int days) {
        return reportService.productPerformance(days);
    }

    @Override
    public List<OrderStatusRow> ordersByStatus(int days) {
        return reportService.ordersByStatus(days);
    }

    @Override
    public List<StockStatus> stock(Long warehouseId) {
        return reportService.stock(warehouseId);
    }

    @Override
    public AfterSalesReport afterSales(int days) {
        return reportService.afterSales(days);
    }
}
