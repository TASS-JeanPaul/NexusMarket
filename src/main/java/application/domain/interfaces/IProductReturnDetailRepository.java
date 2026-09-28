package application.domain.interfaces;

import application.domain.models.ProductReturnDetail;

import java.sql.SQLException;
import java.util.List;

public interface IProductReturnDetailRepository extends IRepository<ProductReturnDetail> {
    List<ProductReturnDetail> findByReturnId(Long returnId) throws SQLException;

    List<ProductReturnDetail> findByOrderDetailId(Long orderDetailId) throws SQLException;
}
