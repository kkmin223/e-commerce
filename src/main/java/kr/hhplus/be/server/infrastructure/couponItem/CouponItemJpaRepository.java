package kr.hhplus.be.server.infrastructure.couponItem;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponItemJpaRepository extends JpaRepository<CouponItem, Long> {
}
