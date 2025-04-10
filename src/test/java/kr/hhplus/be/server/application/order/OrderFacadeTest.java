package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.dataPlatform.DataPlatform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private CouponItemService couponItemService;
    @Mock
    private DataPlatform dataPlatform;

    @Test
    void 주문을_생성하고_결재를_진행한다() {
        // given
        Long userId = 1L;
        Long couponItemId = 1L;
        Long productId = 1L;
        OrderCriteria.OrderProduct orderProduct = OrderCriteria.OrderProduct.of(productId, 1);
        LocalDateTime orderAt = LocalDateTime.now();
        OrderCriteria.OrderAndPay criteria = OrderCriteria.OrderAndPay.of(1L, 1L, List.of(orderProduct), orderAt);

        User user = User.of(userId, 1_000_000);
        Product product = Product.of(productId, "상품", 10, 1_000);
        Map<Product, Integer> productsWithQuantities = Map.of(product, 1);
        Order order = Order.create(user, productsWithQuantities, orderAt);
        CouponItem couponItem = CouponItem.of(user, AmountCoupon.of("쿠폰", 1, 100), false);
        Payment payment = Payment.create(order, user);

        Mockito.when(userService.getUser(any())).thenReturn(user);
        Mockito.when(productService.findProductsWithQuantities(any())).thenReturn(productsWithQuantities);
        Mockito.when(orderService.createOrder(any())).thenReturn(order);
        Mockito.when(couponItemService.getCouponItem(any())).thenReturn(couponItem);
        Mockito.when(paymentService.createAndProcess(any())).thenReturn(payment);


        // when
        OrderResult.OrderAndPay result = orderFacade.orderAndPay(criteria);

        // then
        InOrder inOrder = Mockito.inOrder(orderService, userService, productService, couponItemService, paymentService, dataPlatform);
        inOrder.verify(userService).getUser(any());
        inOrder.verify(productService).findProductsWithQuantities(any());
        inOrder.verify(orderService).createOrder(any());
        inOrder.verify(couponItemService).getCouponItem(any());
        inOrder.verify(paymentService).createAndProcess(any());
        inOrder.verify(dataPlatform).sendData(any());

    }
}
