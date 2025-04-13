package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.CouponResult;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/coupons")
public class CouponController implements CouponApi {

    private final CouponFacade couponFacade;

    @Override
    @PostMapping("/{id}/issue")
    public ResponseEntity<ApiResult<CouponResponse.Coupon>> issueCoupon(long id, CouponRequest.Issue issueRequest) {

        CouponResult.Issue issueResult = couponFacade.IssueCoupon(CouponCriteria.Issue.of(id, issueRequest.getUserId()));

        return ResponseEntity.ok(
            ApiResult.of(SuccessCode.ISSUE_COUPON,
                CouponResponse.Coupon.of(issueResult.getId(), issueResult.getCouponName(), issueResult.getIsUsed(), issueResult.getDiscountLabel(), issueResult.getCouponType())
            )
        );
    }
}
