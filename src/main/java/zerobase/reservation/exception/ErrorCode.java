package zerobase.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    ALREADY_REGISTERED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다"),
    ALREADY_REGISTERED_RESTAURANT(HttpStatus.BAD_REQUEST, "이미 등록된 매장입니다"),
    EARLY_CONFIRMATION(HttpStatus.BAD_REQUEST, "예약 시간 10분 전부터 확정 가능합니다."),
    FULL_CAPACITY(HttpStatus.BAD_REQUEST, "만석 입니다."),
    NO_SUCH_RESERVATION(HttpStatus.BAD_REQUEST, "해당하는 예약이 없습니다."),
    NO_SUCH_RESTAURANT(HttpStatus.BAD_REQUEST, "없는 음식점 입니다"),
    NO_SUCH_REVIEW(HttpStatus.BAD_REQUEST, "없는 리뷰입니다."),
    NO_SUCH_USER(HttpStatus.BAD_REQUEST, "없는 사용자 입니다"),
    REJECTED_RESERVATION(HttpStatus.BAD_REQUEST, "거절된 예약입니다."),
    UNAUTHORIZED(HttpStatus.BAD_REQUEST, "권한이 없습니다."),
    WAIT_RESERVATION(HttpStatus.BAD_REQUEST, "점장의 승인을 기다리는 예약입니다."),
    WRONG_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    WRONG_LOGIN(HttpStatus.BAD_REQUEST, "잘못된 로그인 정보입니다"),
    WRONG_REVIEW(HttpStatus.BAD_REQUEST, "적어도 한 번 이상 이용한 매장만 평가할 수 있습니다");
    
    private final HttpStatus status;
    private final String detail;
}