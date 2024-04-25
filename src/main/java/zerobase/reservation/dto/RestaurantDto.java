package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class RestaurantDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private String name;
        private String location;
        private Integer capacity;
        private String businessNumber;
        private String description;
        private String phoneNumber;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private Long restaurantId;
        private String name;
        private String location;
        private Integer capacity;
        private String phoneNumber;
        private String description;
    }
}