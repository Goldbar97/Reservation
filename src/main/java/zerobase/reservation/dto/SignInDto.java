package zerobase.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class SignInDto {
    
    @Getter
    @Setter
    public static class Request {
        
        private String email;
        private String password;
    }
    
    @Builder
    @Getter
    @Setter
    public static class Response {
        
        private String email;
        private List<String> role;
        private String token;
    }
}
