package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SignUpDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private String email;
        private String name;
        private String password;
        private String phoneNumber;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private Long id;
        private String email;
        private String name;
    }
}
