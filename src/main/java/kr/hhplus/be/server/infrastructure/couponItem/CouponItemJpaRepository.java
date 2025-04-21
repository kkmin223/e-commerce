package kr.hhplus.be.server.infrastructure.couponItem;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponItemJpaRepository extends JpaRepository<CouponItem, Long> {
    List<CouponItem> findByUser(User user);
    int countByCouponId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ci FROM CouponItem ci WHERE ci.id = :id")
    Optional<CouponItem> findByIdForUpdate(Long id);
}
