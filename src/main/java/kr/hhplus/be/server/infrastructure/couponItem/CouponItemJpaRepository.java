package kr.hhplus.be.server.infrastructure.couponItem;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponItemJpaRepository extends JpaRepository<CouponItem, Long> {
    List<CouponItem> findByUser(User user);
}
