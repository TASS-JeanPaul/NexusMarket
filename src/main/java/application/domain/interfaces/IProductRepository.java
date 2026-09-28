package application.domain.interfaces;

import application.domain.enums.ProductStatus;
import application.domain.models.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IProductRepository extends IRepository<Product> {
    Optional<Product> findByCode(String code) throws SQLException;

    List<Product> findBySellerId(Long sellerId) throws SQLException;

    List<Product> findByCategoryId(Long categoryId) throws SQLException;

    List<Product> findByStatus(ProductStatus status) throws SQLException;
}
