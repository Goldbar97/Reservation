package zerobase.reservation.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class ReservationDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private Integer headCount;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime reservedAt;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private boolean visited;
        private Long reservationId;
        private Long customerId;
        private Long restaurantId;
        private Integer headCount;
        private LocalDateTime reservedAt;
    }
}
