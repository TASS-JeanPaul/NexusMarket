package application.models.ports.in;

import application.data.enums.ReturnStatus;
import application.data.enums.Role;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.ports.out.IOrderDetailRepository;
import application.data.ports.out.IProductReturnDetailRepository;
import application.data.ports.out.IProductReturnRepository;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;
import application.data.entities.ProductReturn;
import application.data.entities.ProductReturnDetail;
import application.data.entities.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IReturnUseCase {

    ProductReturn request(Long buyerId, Long orderId, String reason, List<ProductReturnDetail> lines);

    ProductReturn findById(Long id);

    ProductReturn findByIdWithDetails(Long id);

    List<ProductReturn> findAll();

    List<ProductReturn> findByOrder(Long orderId);

    List<ProductReturn> findByBuyer(Long buyerId);

    ProductReturn findByNumber(String returnNumber);

    List<ProductReturnDetail> details(Long returnId);

    ProductReturn approve(Long returnId, Long reviewerId);

    ProductReturn reject(Long returnId, Long reviewerId);

    ProductReturn receive(Long returnId, Long warehouseId, Long userId);

    ProductReturn complete(Long returnId);

    ProductReturn requireRefundable(Long returnId);

    BigDecimal returnedValue(Long returnId);
}
