package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zerobase.reservation.dto.SignUpForm;
import zerobase.reservation.type.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Entity(name = "Manager")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class ManagerEntity implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> role;
    
    private String name;
    private String password;
    private String phoneNumber;
    
    /**
     * mappedBy 는 RestaurantEntity 의 변수 `managerEntity` 에 의해 매핑됩니다.
     * cascade 는 부모 엔티티의 변경이 자식 엔티티에도 영향을 미치는 지 설정합니다.
     */
    @OneToMany(mappedBy = "managerEntity", cascade = CascadeType.ALL, fetch =
            FetchType.LAZY)
    private List<RestaurantEntity> restaurantEntity;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static ManagerEntity from(SignUpForm.Request form) {
        
        List<String> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_MANAGER.getValue());
        
        return ManagerEntity.builder()
                .email(form.getEmail())
                .role(roles)
                .name(form.getName())
                .password(form.getPassword())
                .phoneNumber(form.getPhoneNumber())
                .build();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        return role.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getUsername() {
        
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        
        return false;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        
        return false;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        
        return false;
    }
    
    @Override
    public boolean isEnabled() {
        
        return false;
    }
}