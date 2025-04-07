package kr.hhplus.be.server.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createAndProcess(PaymentCommand.CreateAndProcess command) {
        Payment payment = Payment.create(command.getOrder(), command.getUser());

        if (command.getCouponItem() != null) {
            payment.applyCoupon(command.getCouponItem());
        }

        payment.processPayment();
        return paymentRepository.save(payment);
    }
}
