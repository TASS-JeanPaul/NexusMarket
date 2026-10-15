package application.models.services;

import application.data.enums.ReturnStatus;
import application.data.enums.Role;
import application.shared.exceptions.ResourceNotFoundException;
import application.data.contracts.IOrderDetailRepository;
import application.data.contracts.IProductReturnDetailRepository;
import application.data.contracts.IProductReturnRepository;
import application.data.entities.Order;
import application.data.entities.OrderDetail;
import application.data.entities.Product;
import application.data.entities.ProductReturn;
import application.data.entities.ProductReturnDetail;
import application.data.entities.User;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReturnService extends BaseService {

    private static final Map<ReturnStatus, Set<ReturnStatus>> ALLOWED_TRANSITIONS = Map.of(
            ReturnStatus.REQUESTED, EnumSet.of(ReturnStatus.APPROVED, ReturnStatus.REJECTED),
            ReturnStatus.APPROVED, EnumSet.of(ReturnStatus.RECEIVED),
            ReturnStatus.RECEIVED, EnumSet.of(ReturnStatus.COMPLETED),
            ReturnStatus.REJECTED, EnumSet.noneOf(ReturnStatus.class),
            ReturnStatus.COMPLETED, EnumSet.noneOf(ReturnStatus.class));

    private final IProductReturnRepository returnRepository;
    private final IProductReturnDetailRepository returnDetailRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final CatalogService catalogService;

    public ReturnService(IProductReturnRepository returnRepository,
                         IProductReturnDetailRepository returnDetailRepository,
                         IOrderDetailRepository orderDetailRepository,
                         OrderService orderService,
                         UserService userService,
                         InventoryService inventoryService,
                         CatalogService catalogService) {
        this.returnRepository = returnRepository;
        this.returnDetailRepository = returnDetailRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderService = orderService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.catalogService = catalogService;
    }

    public ProductReturn request(Long buyerId, Long orderId, String reason, List<ProductReturnDetail> lines) {
        return execute(() -> {
            Order order = orderService.requireReturnable(orderId);
            require(order.getBuyerId().equals(buyerId),
                    "The order " + orderId + " does not belong to the buyer " + buyerId);
            requireText(reason, "The reason for the return");
            require(lines != null && !lines.isEmpty(), "At least one product to return is required");

            for (ProductReturnDetail line : lines) {
                validateLine(orderId, line);
            }

            ProductReturn productReturn = new ProductReturn();
            productReturn.setReturnNumber(generateDocumentNumber("RET"));
            productReturn.setOrderId(orderId);
            productReturn.setBuyerId(buyerId);
            productReturn.setReason(reason);
            productReturn.setStatus(ReturnStatus.REQUESTED);
            productReturn.setRequestedAt(LocalDateTime.now());

            ProductReturn stored = returnRepository.save(productReturn);

            for (ProductReturnDetail line : lines) {
                line.setReturnId(stored.getId());
                returnDetailRepository.save(line);
            }

            stored.setDetails(lines);
            return stored;
        });
    }

    public ProductReturn findById(Long id) {
        requireIdentifier(id, "The return identifier");
        return execute(() -> findOrFail(returnRepository.findById(id), "Return", id));
    }

    public ProductReturn findByIdWithDetails(Long id) {
        return execute(() -> {
            ProductReturn productReturn = findById(id);
            productReturn.setDetails(returnDetailRepository.findByReturnId(id));
            return productReturn;
        });
    }

    public List<ProductReturn> findAll() {
        return execute(returnRepository::findAll);
    }

    public List<ProductReturn> findByOrder(Long orderId) {
        requireIdentifier(orderId, "The order identifier");
        return execute(() -> returnRepository.findByOrderId(orderId));
    }

    public List<ProductReturn> findByBuyer(Long buyerId) {
        requireIdentifier(buyerId, "The buyer identifier");
        return execute(() -> returnRepository.findByBuyerId(buyerId));
    }

    public ProductReturn findByNumber(String returnNumber) {
        requireText(returnNumber, "The return number");
        return execute(() -> returnRepository.findByReturnNumber(returnNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No return is registered with the number " + returnNumber)));
    }

    public List<ProductReturnDetail> details(Long returnId) {
        findById(returnId);
        return execute(() -> returnDetailRepository.findByReturnId(returnId));
    }

    public ProductReturn approve(Long returnId, Long reviewerId) {
        return execute(() -> {
            requireReviewer(reviewerId);
            ProductReturn productReturn = transition(returnId, ReturnStatus.APPROVED);

            productReturn.setReviewedById(reviewerId);
            returnRepository.update(productReturn);
            return productReturn;
        });
    }

    public ProductReturn reject(Long returnId, Long reviewerId) {
        return execute(() -> {
            requireReviewer(reviewerId);
            ProductReturn productReturn = transition(returnId, ReturnStatus.REJECTED);

            productReturn.setReviewedById(reviewerId);
            productReturn.setClosedAt(LocalDateTime.now());
            returnRepository.update(productReturn);
            return productReturn;
        });
    }

    public ProductReturn receive(Long returnId, Long warehouseId, Long userId) {
        return execute(() -> {
            ProductReturn productReturn = transition(returnId, ReturnStatus.RECEIVED);

            for (ProductReturnDetail line : returnDetailRepository.findByReturnId(returnId)) {
                OrderDetail orderDetail = findOrderDetail(line.getOrderDetailId());
                Product product = catalogService.findProductById(orderDetail.getProductId());

                if (product.managesInventory()) {
                    requireIdentifier(warehouseId, "The warehouse identifier that receives the merchandise");
                    inventoryService.returnToStock(orderDetail.getProductId(), orderDetail.getVariantId(),
                            warehouseId, line.getReturnedQuantity(), userId);
                }
            }
            return productReturn;
        });
    }

    public ProductReturn complete(Long returnId) {
        return execute(() -> {
            ProductReturn productReturn = transition(returnId, ReturnStatus.COMPLETED);

            productReturn.setClosedAt(LocalDateTime.now());
            returnRepository.update(productReturn);
            return productReturn;
        });
    }

    public ProductReturn requireRefundable(Long returnId) {
        ProductReturn productReturn = findById(returnId);
        require(productReturn.getStatus() == ReturnStatus.APPROVED
                        || productReturn.getStatus() == ReturnStatus.RECEIVED
                        || productReturn.getStatus() == ReturnStatus.COMPLETED,
                "The return " + returnId + " is " + productReturn.getStatus()
                        + ", so it does not admit a refund yet");
        return productReturn;
    }

    public BigDecimal returnedValue(Long returnId) {
        return execute(() -> {
            BigDecimal value = BigDecimal.ZERO;

            for (ProductReturnDetail line : returnDetailRepository.findByReturnId(returnId)) {
                OrderDetail orderDetail = findOrderDetail(line.getOrderDetailId());
                value = value.add(orderDetail.getUnitPrice()
                        .multiply(BigDecimal.valueOf(line.getReturnedQuantity())));
            }
            return value;
        });
    }

    private void validateLine(Long orderId, ProductReturnDetail line) {
        execute(() -> {
            require(line != null, "The return line is required");
            requirePositive(line.getReturnedQuantity(), "The returned quantity");

            OrderDetail orderDetail = findOrderDetail(line.getOrderDetailId());
            require(orderId.equals(orderDetail.getOrderId()),
                    "The order line " + line.getOrderDetailId() + " does not belong to the order " + orderId);

            int alreadyReturned = 0;
            for (ProductReturnDetail previous : returnDetailRepository
                    .findByOrderDetailId(line.getOrderDetailId())) {
                alreadyReturned += previous.getReturnedQuantity();
            }

            int returnable = orderDetail.getQuantity() - alreadyReturned;
            require(line.getReturnedQuantity() <= returnable,
                    "Only " + returnable + " units of " + orderDetail.getProductName()
                            + " can still be returned, and " + line.getReturnedQuantity() + " were requested");
            return true;
        });
    }

    private OrderDetail findOrderDetail(Long orderDetailId) {
        requireIdentifier(orderDetailId, "The order line identifier");
        return execute(() ->
                findOrFail(orderDetailRepository.findById(orderDetailId), "Order line", orderDetailId));
    }

    private void requireReviewer(Long reviewerId) {
        User reviewer = userService.findActiveOrFail(reviewerId);
        require(reviewer.getRole() == Role.ADMINISTRATOR || reviewer.getRole() == Role.SUPERVISOR,
                "Only an administrator or a supervisor can review a return");
    }

    private ProductReturn transition(Long returnId, ReturnStatus target) {
        return execute(() -> {
            ProductReturn productReturn = findById(returnId);
            require(ALLOWED_TRANSITIONS.get(productReturn.getStatus()).contains(target),
                    "A return cannot move from " + productReturn.getStatus() + " to " + target);

            productReturn.setStatus(target);
            returnRepository.update(productReturn);
            return productReturn;
        });
    }
}
