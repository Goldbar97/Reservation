package zerobase.reservation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum UserRole {

    ROLE_CUSTOMER("ROLE_CUSTOMER"),
    ROLE_MANAGER("ROLE_MANAGER");
    
    private String value;
}
