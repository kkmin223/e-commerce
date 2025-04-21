package kr.hhplus.be.server.application.order;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemCommand;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.dataPlatform.DataPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Facade
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponItemService couponItemService;
    private final DataPlatform dataPlatform;

    @Retryable(
        value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class}, // 재시도할 예외
        maxAttempts = 5,
        backoff = @Backoff(delay = 100) // 재시도 간 딜레이(ms),
    )
    @Transactional
    public OrderResult.OrderAndPay orderAndPay(OrderCriteria.OrderAndPay criteria) {

        User user = userService.getUserForUpdate(UserCommand.Get.of(criteria.getUserId()));

        Map<Product, Integer> productsWithQuantities = productService.findProductsWithQuantities(ProductCommand.FindProductsWithQuantity.of(criteria.getOrderProducts()));

        Order order = orderService.createOrder(OrderCommand.CreateOrder.of(user, productsWithQuantities, criteria.getOrderAt()));

        CouponItem couponItem = null;
        if (criteria.getCouponItemId() != null) {
            couponItem = couponItemService.getCouponItem(CouponItemCommand.Get.of(criteria.getCouponItemId()));
        }

        Payment payment = paymentService.createAndProcess(PaymentCommand.CreateAndProcess.of(user, order, couponItem));

        dataPlatform.sendData(order);

        return OrderResult.OrderAndPay.createdBy(order);
    }
}
