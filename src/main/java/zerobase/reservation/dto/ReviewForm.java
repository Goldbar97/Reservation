package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ReviewForm {
    
    @Getter
    @Setter
    public static class Request {
        
        private String description;
        private Integer rate;
    }
    
    @Getter
    @Builder
    public static class Response {
        
        private Long reviewId;
    }
}
