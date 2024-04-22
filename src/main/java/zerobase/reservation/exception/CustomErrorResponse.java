package zerobase.reservation.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomErrorResponse {
    
    private int code;
    private String message;
}