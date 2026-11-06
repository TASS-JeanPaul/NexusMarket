package application.models.services;

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

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService extends BaseService {

    private static final int PERCENT_SCALE = 1;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal EXCELLENT_RETURN_RATE = BigDecimal.valueOf(2);
    private static final BigDecimal SOLID_RETURN_RATE = BigDecimal.valueOf(8);
    private static final BigDecimal WATCH_RETURN_RATE = BigDecimal.valueOf(20);
    private static final int CRITICAL_STOCK = 5;
    private static final int LOW_STOCK = 15;
    private static final long STAR_UNITS = 10;
    private static final long STEADY_UNITS = 3;

    private final IReportRepository reportRepository;

    public ReportService(IReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<SellerPerformance> sellerPerformance(int days) {
        LocalDateTime since = windowStart(days);

        return execute(() -> {
            List<SellerPerformance> performances = new ArrayList<>();

            for (SellerSalesRow sales : reportRepository.salesBySeller(since)) {
                BigDecimal returnRate = percentage(sales.unitsReturned(), sales.unitsSold());
                performances.add(new SellerPerformance(sales, returnRate, standingFor(returnRate)));
            }
            return performances;
        });
    }

    public List<CategoryShare> categoryShare(int days) {
        LocalDateTime since = windowStart(days);

        return execute(() -> {
            List<CategorySalesRow> rows = reportRepository.salesByCategory(since);
            BigDecimal total = BigDecimal.ZERO;

            for (CategorySalesRow row : rows) {
                total = total.add(row.revenue());
            }

            List<CategoryShare> shares = new ArrayList<>();
            for (CategorySalesRow row : rows) {
                shares.add(new CategoryShare(row, proportion(row.revenue(), total)));
            }
            return shares;
        });
    }

    public List<ProductPerformance> productPerformance(int days) {
        LocalDateTime since = windowStart(days);

        return execute(() -> {
            List<ProductPerformance> performances = new ArrayList<>();

            for (ProductSalesRow sales : reportRepository.salesByProduct(since)) {
                performances.add(classify(sales));
            }
            return performances;
        });
    }

    public List<OrderStatusRow> ordersByStatus(int days) {
        LocalDateTime since = windowStart(days);
        return execute(() -> reportRepository.ordersByStatus(since));
    }

    public List<StockStatus> stock(Long warehouseId) {
        return execute(() -> {
            List<WarehouseStockRow> rows = warehouseId == null
                    ? reportRepository.stock()
                    : reportRepository.stockByWarehouse(warehouseId);

            List<StockStatus> statuses = new ArrayList<>();
            for (WarehouseStockRow row : rows) {
                statuses.add(new StockStatus(row, levelFor(row.available())));
            }
            return statuses;
        });
    }

    public AfterSalesReport afterSales(int days) {
        LocalDateTime since = windowStart(days);

        return execute(() -> {
            List<CarrierQuality> carriers = new ArrayList<>();

            for (CarrierPerformanceRow row : reportRepository.carrierPerformance(since)) {
                carriers.add(new CarrierQuality(row, percentage(row.failed(), row.shipments())));
            }
            return new AfterSalesReport(reportRepository.returnsByProduct(since), carriers);
        });
    }

    private LocalDateTime windowStart(int days) {
        requirePositive(days, "The number of days of the analysis window");
        return LocalDateTime.now().minusDays(days);
    }

    private BigDecimal percentage(long part, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(PERCENT_SCALE, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(part)
                .multiply(HUNDRED)
                .divide(BigDecimal.valueOf(total), PERCENT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal proportion(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(PERCENT_SCALE, RoundingMode.HALF_UP);
        }
        return part.multiply(HUNDRED).divide(total, PERCENT_SCALE, RoundingMode.HALF_UP);
    }

    private SellerPerformance.Standing standingFor(BigDecimal returnRate) {
        if (returnRate.compareTo(EXCELLENT_RETURN_RATE) <= 0) {
            return SellerPerformance.Standing.EXCELLENT;
        }
        if (returnRate.compareTo(SOLID_RETURN_RATE) <= 0) {
            return SellerPerformance.Standing.SOLID;
        }
        if (returnRate.compareTo(WATCH_RETURN_RATE) <= 0) {
            return SellerPerformance.Standing.WATCH;
        }
        return SellerPerformance.Standing.AT_RISK;
    }

    private StockStatus.Level levelFor(int available) {
        if (available == 0) {
            return StockStatus.Level.OUT_OF_STOCK;
        }
        if (available <= CRITICAL_STOCK) {
            return StockStatus.Level.CRITICAL;
        }
        if (available <= LOW_STOCK) {
            return StockStatus.Level.LOW;
        }
        return StockStatus.Level.HEALTHY;
    }

    private ProductPerformance classify(ProductSalesRow sales) {
        if (sales.unitsSold() >= STAR_UNITS) {
            return new ProductPerformance(sales, ProductPerformance.Classification.STAR,
                    "Mantener existencias y considerar impulsarlo");
        }
        if (sales.unitsSold() >= STEADY_UNITS) {
            return new ProductPerformance(sales, ProductPerformance.Classification.STEADY,
                    "Mantener sin cambios");
        }
        if (sales.unitsSold() > 0) {
            return new ProductPerformance(sales, ProductPerformance.Classification.SLOW,
                    "Revisar precio o presentacion");
        }
        return new ProductPerformance(sales, ProductPerformance.Classification.DEAD,
                "Sin ventas en la ventana: evaluar descontinuarlo");
    }
}
