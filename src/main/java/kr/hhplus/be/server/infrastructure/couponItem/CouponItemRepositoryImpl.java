package kr.hhplus.be.server.infrastructure.couponItem;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CouponItemRepositoryImpl implements CouponItemRepository {

    private final CouponItemJpaRepository couponItemJpaRepository;

    @Override
    public Optional<CouponItem> findById(Long id) {
        return couponItemJpaRepository.findById(id);
    }

    @Override
    public CouponItem save(CouponItem couponItem) {
        return couponItemJpaRepository.save(couponItem);
    }

    @Override
    public List<CouponItem> findByUser(User user) {
        return couponItemJpaRepository.findByUser(user);
    }

    @Override
    public int countByCouponId(Long id) {
        return couponItemJpaRepository.countByCouponId(id);
    }

    @Override
    public Optional<CouponItem> findByIdForUpdate(Long id) {
        return couponItemJpaRepository.findByIdForUpdate(id);
    }
}
