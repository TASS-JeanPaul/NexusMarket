package application.models.usecases;

import application.models.ports.in.IReturnUseCase;
import application.models.services.ReturnService;
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

import org.springframework.stereotype.Service;

@Service
public class ReturnUseCase implements IReturnUseCase {

    private final ReturnService returnService;

    public ReturnUseCase(ReturnService returnService) {
        this.returnService = returnService;
    }

    @Override
    public ProductReturn request(Long buyerId, Long orderId, String reason, List<ProductReturnDetail> lines) {
        return returnService.request(buyerId, orderId, reason, lines);
    }

    @Override
    public ProductReturn findById(Long id) {
        return returnService.findById(id);
    }

    @Override
    public ProductReturn findByIdWithDetails(Long id) {
        return returnService.findByIdWithDetails(id);
    }

    @Override
    public List<ProductReturn> findAll() {
        return returnService.findAll();
    }

    @Override
    public List<ProductReturn> findByOrder(Long orderId) {
        return returnService.findByOrder(orderId);
    }

    @Override
    public List<ProductReturn> findByBuyer(Long buyerId) {
        return returnService.findByBuyer(buyerId);
    }

    @Override
    public ProductReturn findByNumber(String returnNumber) {
        return returnService.findByNumber(returnNumber);
    }

    @Override
    public List<ProductReturnDetail> details(Long returnId) {
        return returnService.details(returnId);
    }

    @Override
    public ProductReturn approve(Long returnId, Long reviewerId) {
        return returnService.approve(returnId, reviewerId);
    }

    @Override
    public ProductReturn reject(Long returnId, Long reviewerId) {
        return returnService.reject(returnId, reviewerId);
    }

    @Override
    public ProductReturn receive(Long returnId, Long warehouseId, Long userId) {
        return returnService.receive(returnId, warehouseId, userId);
    }

    @Override
    public ProductReturn complete(Long returnId) {
        return returnService.complete(returnId);
    }

    @Override
    public ProductReturn requireRefundable(Long returnId) {
        return returnService.requireRefundable(returnId);
    }

    @Override
    public BigDecimal returnedValue(Long returnId) {
        return returnService.returnedValue(returnId);
    }
}
