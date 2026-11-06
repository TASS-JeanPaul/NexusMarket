package application.models.ports.in;

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

public interface IReportUseCase {

    List<SellerPerformance> sellerPerformance(int days);

    List<CategoryShare> categoryShare(int days);

    List<ProductPerformance> productPerformance(int days);

    List<OrderStatusRow> ordersByStatus(int days);

    List<StockStatus> stock(Long warehouseId);

    AfterSalesReport afterSales(int days);
}
