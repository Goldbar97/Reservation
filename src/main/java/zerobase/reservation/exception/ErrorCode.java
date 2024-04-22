package zerobase.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    ALREADY_REGISTERED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다"),
    ALREADY_REGISTERED_RESTAURANT(HttpStatus.BAD_REQUEST, "이미 등록된 매장입니다"),
    WRONG_LOGIN(HttpStatus.BAD_REQUEST, "잘못된 로그인 정보입니다"),
    NO_SUCH_RESTAURANT(HttpStatus.BAD_REQUEST, "없는 음식점 입니다"),
    WRONG_REVIEW(HttpStatus.BAD_REQUEST, "적어도 한 번 이상 이용한 매장만 평가할 수 있습니다"),
    NO_SUCH_USER(HttpStatus.BAD_REQUEST, "없는 사용자 입니다");
    
    private final HttpStatus status;
    private final String detail;
}