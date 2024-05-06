package zerobase.reservation.type;

import lombok.AllArgsConstructor;
import zerobase.reservation.exception.CustomException;
import zerobase.reservation.exception.ErrorCode;

@AllArgsConstructor
public enum ReservationStatus {
    WAIT("WAIT"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");
    
    private final String value;
    
    public static ReservationStatus from(String str) {
        
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.value.equalsIgnoreCase(str)) {
                return status;
            }
        }
        
        throw new CustomException(ErrorCode.WRONG_INPUT);
    }
}
