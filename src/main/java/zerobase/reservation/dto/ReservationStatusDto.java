package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zerobase.reservation.type.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationStatusDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private String status;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private ReservationStatus status;
        private boolean visited;
        private Long reservationId;
        private Long customerId;
        private Long restaurantId;
        private Integer headCount;
        private LocalDateTime reservedAt;
    }
}
