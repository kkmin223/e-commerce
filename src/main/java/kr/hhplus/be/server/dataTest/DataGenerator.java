package kr.hhplus.be.server.dataTest;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.PercentageCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.user.User;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.stream.IntStream;

public class DataGenerator {
    public List<User> generateUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> Instancio.of(User.class)
                .create())
            .toList();
    }

    public List<Coupon> generateCoupons(int count) {
        return (List<Coupon>) IntStream.range(0, count)
            .mapToObj(i -> Instancio.of(randomCouponSubtype())
                .generate(Select.field(Coupon.class, "title"), gen -> gen.text().pattern("#C-10##")) // 상위 클래스 명시
                .generate(Select.field(Coupon.class, "initialQuantity"), gen -> gen.ints().range(100, 1000))
                .generate(Select.field(Coupon.class, "remainingQuantity"), gen -> gen.ints().range(0, 1000))
                .create())
            .toList();
    }

    public List<CouponItem> generateTopUserCouponItems(List<User> users, List<Coupon> coupons, int topUserCount, int itemsPerTopUser) {
        List<User> topUsers = users.subList(0, topUserCount); // 상위 10명 선택

        return topUsers.stream()
            .flatMap(user -> IntStream.range(0, itemsPerTopUser)
                .mapToObj(i -> Instancio.of(CouponItem.class)
                    .set(Select.field("user"), user)
                    .set(Select.field("coupon"), randomCoupon(coupons))
                    .generate(Select.field("isUsed"), gen -> gen.booleans())
                    .create()))
            .toList();
    }

    public List<CouponItem> generateCouponItems(List<User> users, List<Coupon> coupons, int perUser) {
        return users.stream()
            .flatMap(user -> IntStream.range(0, perUser)
                .mapToObj(i -> Instancio.of(CouponItem.class)
                    .set(Select.field("user"), user)
                    .set(Select.field("coupon"), randomCoupon(coupons))
                    .generate(Select.field("isUsed"), gen -> gen.booleans())
                    .create()))
            .toList();
    }

    private Class<? extends Coupon> randomCouponSubtype() {
        return Math.random() > 0.5 ? AmountCoupon.class : PercentageCoupon.class;
    }

    private Coupon randomCoupon(List<Coupon> coupons) {
        return coupons.get((int) (Math.random() * coupons.size()));
    }
}
