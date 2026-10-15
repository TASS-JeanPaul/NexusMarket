package application.data.contracts;

import application.data.entities.ProductReturnDetail;

import java.sql.SQLException;
import java.util.List;

public interface IProductReturnDetailRepository extends IRepository<ProductReturnDetail> {
    List<ProductReturnDetail> findByReturnId(Long returnId) throws SQLException;

    List<ProductReturnDetail> findByOrderDetailId(Long orderDetailId) throws SQLException;
}
