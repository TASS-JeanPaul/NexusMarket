package application.models.reports;

import application.data.views.CategorySalesRow;

import java.math.BigDecimal;

public record CategoryShare(CategorySalesRow sales, BigDecimal sharePercent) {
}
