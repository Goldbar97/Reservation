package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ReviewDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private String description;
        private Integer rate;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private Long reviewId;
    }
}
