package zerobase.reservation.dto;

import lombok.*;

public class RestaurantForm {
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        
        private String name;
        private String location;
        private Integer capacity;
        private String businessNumber;
        private String description;
        private String phoneNumber;
    }
    
    @Getter
    @Setter
    @Builder
    public static class Response {
        
        private Long restaurantId;
    }
}
