package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zerobase.reservation.dto.SignUpDto;
import zerobase.reservation.type.UserRole;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Entity(name = "Manager")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Setter
public class ManagerEntity implements UserDetails, ProjectEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> role;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
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
    
    public static ManagerEntity from(SignUpDto.Request form) {
        
        return ManagerEntity.builder()
                .email(form.getEmail())
                .role(new ArrayList<>(
                        Arrays.asList(UserRole.ROLE_MANAGER.toString())))
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