package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zerobase.reservation.type.UserRole;

public class SignUpForm {
    
    @Getter
    @Setter
    public static class Request {
        
        private String email;
        private String name;
        private String password;
        private String phoneNumber;
    }
    
    @Getter
    @Builder
    public static class Response {
        
        private Long id;
        private String email;
        private String name;
    }
}
