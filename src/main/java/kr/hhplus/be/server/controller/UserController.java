package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.api.UserApi;
import kr.hhplus.be.server.dto.AmountResponseDto;
import kr.hhplus.be.server.dto.ChargeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {
    @Override
    public ResponseEntity<AmountResponseDto> getUserAmount(Long id) {
        return ResponseEntity.ok(new AmountResponseDto(id, 5000));
    }

    @Override
    public ResponseEntity<AmountResponseDto> chargeUserAmount(Long id, ChargeRequestDto chargeRequestDto) {
        return ResponseEntity.ok(new AmountResponseDto(id, 5000));
    }


}
