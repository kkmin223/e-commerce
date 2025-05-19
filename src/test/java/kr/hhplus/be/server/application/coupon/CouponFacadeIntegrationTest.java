package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }


    @Test
    void 선착순_쿠폰을_발급한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000));
        Integer initialQuantity = 10;
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", initialQuantity));

        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(savedUser.getId(), savedCoupon.getId());

        // when
        CouponResult.Issue result = couponFacade.IssueCoupon(criteria);

        // then
        // 결과 검증
        assertThat(result)
            .extracting(CouponResult.Issue::getCouponName, CouponResult.Issue::getIsUsed, CouponResult.Issue::getDiscountLabel, CouponResult.Issue::getCouponType)
            .containsExactly(savedCoupon.getTitle(), Boolean.FALSE, savedCoupon.getDiscountLabel(), savedCoupon.getCouponType());

        // 쿠폰 검증
        assertThat(savedCoupon.getRemainingQuantity())
            .isEqualTo(initialQuantity - 1);

        // 쿠폰 아이템 검증
        Optional<CouponItem> expectedCouponItem = couponItemRepository.findById(result.getId());

        assertThat(expectedCouponItem)
            .isNotNull();

        assertThat(expectedCouponItem.get())
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(savedUser, savedCoupon, Boolean.FALSE);
    }

    @Test
    void 쿠폰_발급을_요청한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000));
        Integer initialQuantity = 10;
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", initialQuantity));
        LocalDateTime issuedAt = LocalDateTime.now();

        CouponCriteria.Request request = CouponCriteria.Request.of(savedUser.getId(), savedCoupon.getId(), issuedAt);

        String key = "COUPON:REQUEST:" + savedCoupon.getId();

        // when
        couponFacade.requestCoupon(request);

        // then
        Set<String> excepted = redisTemplate.opsForZSet().range(key, 0, -1);
        assertThat(excepted)
            .containsExactlyInAnyOrder(
                savedUser.getId().toString()
            );
    }

    @Test
    void 쿠폰_대기열을_발급_처리한다() {
        // given
        User savedUser1 = userRepository.save(User.of(1_000));
        User savedUser2 = userRepository.save(User.of(1_000));
        LocalDateTime issuedAt1 = LocalDateTime.now();
        LocalDateTime issuedAt2 = issuedAt1.plusMinutes(1);
        Integer initialQuantity = 10;
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", initialQuantity));

        CouponCriteria.Process criteria = CouponCriteria.Process.of(1);
        String key = "COUPON:REQUEST:" + savedCoupon.getId();
        redisTemplate.opsForZSet().add(key, savedUser1.getId().toString(), issuedAt1.toEpochSecond(ZoneOffset.UTC));
        redisTemplate.opsForZSet().add(key, savedUser2.getId().toString(), issuedAt2.toEpochSecond(ZoneOffset.UTC));

        // when
        couponFacade.processCouponRequest(criteria);

        // then
        List<CouponItem> user1CouponItem = couponItemRepository.findByUser(savedUser1);
        List<CouponItem> user2CouponItem = couponItemRepository.findByUser(savedUser2);

        assertThat(user1CouponItem)
            .hasSize(1);

        assertThat(user2CouponItem)
            .isEmpty();

        Set<String> issueRequestUserIds = redisTemplate.opsForZSet().range(key, 0, -1);
        assertThat(issueRequestUserIds)
            .hasSize(1)
            .contains(savedUser2.getId().toString());
    }

    @Test
    void 쿠폰_대기열에_중복이_있을때_1개만_발급된다() {
        // given
        User savedUser1 = userRepository.save(User.of(1_000));
        LocalDateTime issuedAt1 = LocalDateTime.now();
        LocalDateTime issuedAt2 = issuedAt1.plusMinutes(1);
        Integer initialQuantity = 10;
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", initialQuantity));

        CouponCriteria.Process criteria = CouponCriteria.Process.of(2);
        String key = "COUPON:REQUEST:" + savedCoupon.getId();
        redisTemplate.opsForZSet().add(key, savedUser1.getId().toString(), issuedAt1.toEpochSecond(ZoneOffset.UTC));
        redisTemplate.opsForZSet().add(key, savedUser1.getId().toString(), issuedAt2.toEpochSecond(ZoneOffset.UTC));

        // when
        couponFacade.processCouponRequest(criteria);

        // then
        List<CouponItem> user1CouponItem = couponItemRepository.findByUser(savedUser1);

        assertThat(user1CouponItem)
            .hasSize(1);
    }
}
