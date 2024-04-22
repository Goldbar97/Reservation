package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class ReservationForm {
    
    @Getter
    @Setter
    public static class Request {
        
        private Integer headCount;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime reservedAt;
    }
    
    @Getter
    @Builder
    public static class Response {
        
        private Long reservationId;
        private Long customerId;
        private Long restaurantId;
        private LocalDateTime reservedAt;
    }
}
