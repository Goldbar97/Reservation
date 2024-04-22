package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SignInForm {
    
    @Getter
    @Setter
    public static class Request {
        
        private String email;
        private String password;
    }
    
    @Getter
    @Setter
    @Builder
    public static class Response {
        
        private String email;
        private List<String> role;
        private String token;
    }
}
